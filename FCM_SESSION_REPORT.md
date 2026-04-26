# FCM Session Flow Report — 2026-04-25

Closes the 9-phase plan at `~/.claude/plans/fcm-full-scale.md`. Continued from the 2026-04-21 session that paused mid-Phase 1 on a Flyway diagnosis.

## What was blocking us coming in

- **Flyway wasn't running** despite `flyway-core` being on the runtime classpath and `spring.flyway.enabled=true`. No log lines, no `flyway_schema_history` table.
- A stale `data/tododb.mv.db` from before V1 baseline — schema validation crashed with `missing column [avatar_bytes] in table [family_groups]`.

## What unblocked it

1. **`spring-boot-flyway` autoconfig dep added** to `build.gradle.kts`. Spring Boot 4.0 split integration autoconfigs (Flyway, Batch, Quartz, etc.) into individual modules; bringing the integration library is no longer enough. Same pattern as the existing `spring-boot-h2console` line.
2. **`NotificationService` switched** from `com.fasterxml.jackson.databind.ObjectMapper` (Jackson 2) to `tools.jackson.databind.ObjectMapper` (Jackson 3 — SB 4.0's only registered ObjectMapper bean lives there).
3. **Stale H2 DB wiped** so V1→V5 ran on a clean schema.

After these three changes, backend boots clean and Flyway applies V1→V5 migrations on every startup.

---

## End-to-end flows now wired

### Flow A — Real invitation accept/decline

```
Alice (admin)               Backend                        Bob (invitee)
─────────────────────────────────────────────────────────────────────────
POST /family-groups/         ┌─ InvitationService.invite ─┐
     invitations             │  • require admin            │
     {groupId,email}         │  • dedup PENDING            │
                             │  • save InvitationEntity    │
                             │  • NotificationPublisher    │
                             │       .publish(             │
                             │         INVITATION_RECEIVED)│
                             │     ↓                        │
                             │   inbox row + data-only push│ ──► onMessageReceived
                                                                ↓
                                                            data-only banner
                                                            (suppressed if on
                                                             /invitations route)
                                                                ↓
                                                            tap → Screen.Invitations
                                                                ↓
                                                            POST /accept
                             ┌─ InvitationService.accept ──┐
                             │  • flip status=ACCEPTED     │
                             │  • insert GroupMemberEntity │
                             │  • activity MEMBER_ADDED    │
                             │  • publish                  │
                             │      INVITATION_ACCEPTED    │
                             │       to inviter             │
                             │     ↓                        │
                             │   alice's inbox row + push  │ ◄── Alice gets
                                                                "Bob joined"
```

Decline path is symmetric: status=DECLINED, push to inviter, no member added.

### Flow B — Task assignment / completion / due-soon

```
A creates task with assigneeId=B              B creates+assigns task
        ↓                                            ↓
GroupTaskService publishes                  same
TASK_ASSIGNED (skipping self-assign)               
        ↓                                            ↓
data-only push to B  ──►  B's FCM service:
                          • inbox row (already written by publisher)
                          • banner UNLESS RouteArgs.GroupTaskDetail
                            currently == (groupId, taskId)
                          • on banner tap: deep-link to GroupTaskDetail

Anyone other than the creator marks isCompleted=true
        ↓
GroupTaskService publishes TASK_COMPLETED → creator
        ↓                                            
        same banner / suppression / deep-link logic

@Scheduled TaskDueSoonJob (every 5 min)
  • SELECT tasks WHERE familyGroupId IS NOT NULL
                AND assignedToUserId IS NOT NULL
                AND isCompleted = false
                AND dueSoonNotifiedAt IS NULL
                AND date BETWEEN today-1 AND today+1
  • compute due Instant from (date, timeEnd)
  • if due in next 20 min → publish TASK_DUE_SOON → assignee
  • set dueSoonNotifiedAt = now (dedup)
```

### Flow C — Master push toggle

```
Settings screen ──Switch──► UserRepository.setPushEnabled(false)
                                    ↓
                         PUT /users/me/preferences
                                    ↓
                         UserPreferencesEntity row updated
                                    ↓
                  later: NotificationPublisher.publish(...)
                                    ↓
                  • inbox row WRITTEN for everyone (you keep history)
                  • UserPreferencesService.pushEnabledUserIds(...)
                    filters out the opted-out user
                  • PushService.sendDataOnly only sees opted-in users
                                    ↓
                  no banner on the user's device, but
                  next time they open the bell → entries are there
```

### Flow D — Bell badge + Notifications inbox

```
TopBarViewModel observes NotificationRepository.unreadCount: StateFlow<Int>
                                    ↓
              TDTopBar bell renders red dot when count > 0
                                    ↓
                       user taps the bell
                                    ↓
       fetchUnreadCount() (cheap server probe) + navigate
                                    ↓
                   Screen.Notifications
                                    ↓
       NotificationsViewModel.refresh() — TTL 30s, force=false
                                    ↓
                   collect repository.notifications: StateFlow
                                    ↓
       per-row tap:
         INVITATION_RECEIVED   → Screen.Invitations
         INVITATION_ACCEPTED   → Screen.GroupDetail
         INVITATION_DECLINED   → Screen.GroupDetail
         TASK_ASSIGNED         → Screen.GroupTaskDetail
         TASK_COMPLETED        → Screen.GroupTaskDetail
         TASK_DUE_SOON         → Screen.GroupTaskDetail
         UNKNOWN               → no-op
       (mark-read happens optimistically with rollback on failure)
```

### Flow E — Current-screen suppression

```
NavController.currentBackStackEntryAsState() in MainContent
                          ↓
parses route + RouteArgs.GroupTaskDetail(groupId, taskId)
                          ↓
mainViewModel.updateCurrentRoute(...)
                          ↓
CurrentRouteTracker (Hilt @Singleton)
   route: StateFlow<String?>
   args:  StateFlow<RouteArgs?>
                          ↓
TDFireBaseMessagingService.onMessageReceived
   reads tracker:
     • TASK_* with matching (groupId, taskId)? → silent refresh, no banner
     • INVITATION_RECEIVED while on Invitations? → silent refresh, no banner
     • everything else → emit banner with proper deep-link extras
```

---

## Surface area — what's new on the wire

### Backend endpoints added

```
POST   /family-groups/invitations                  create + push to invitee
GET    /family-groups/invitations/me               list caller's pending
POST   /family-groups/invitations/{id}/accept      join group, push to inviter
POST   /family-groups/invitations/{id}/decline     push to inviter
DELETE /family-groups/invitations/{id}             inviter cancels

GET    /notifications?limit=&before=              cursor-paginated inbox
PUT    /notifications/{id}/read
PUT    /notifications/read-all
GET    /notifications/unread-count

GET    /users/me/preferences                       { pushEnabled }
PUT    /users/me/preferences                       { pushEnabled }
```

### Backend tables added

- `group_invitations` (id, groupId, inviterUserId, inviteeUserId, inviteeEmail, status, createdAt, respondedAt) + idx on (inviteeUserId,status), idx on groupId
- `notifications` (was already in V2 from prior session — now actively populated)
- `user_preferences` (userId PK, pushEnabled, updatedAt)
- `tasks.due_soon_notified_at` column for scheduler dedup

### Backend background jobs added

- `TaskDueSoonJob` — `@Scheduled fixedDelay = 5 min`
- `NotificationRetentionJob` — `@Scheduled cron = "0 0 3 * * *"` UTC

### Client surface added

- New screens: `Screen.Notifications`, `Screen.Invitations` (both registered in `AppDestination` so `TDTopBar` shows back-arrow + title for free)
- New singleton: `CurrentRouteTracker`
- New repositories: `NotificationRepository`, `InvitationRepository`
- Top bar bell: `TDTopBarAction.unreadBadgeCount` → red dot overlay
- Settings toggle: master `Push notifications` switch (auth-gated)
- 5 new typed `PushPayload` cases + parser updates

---

## What's verified

- ✅ Backend boots clean — Flyway V1→V5 applied, Tomcat on :8080, no errors
- ✅ App compiles clean (`:app:compileDebugKotlin BUILD SUCCESSFUL`) — only pre-existing deprecation warnings unrelated to this work
- ✅ 17/20 backend e2e steps green via curl; remaining 3 are sad-path codes mangled by a pre-existing global error-page security issue (server logs show correct 400/409 from controllers — wire returns 403 due to `/error` forward access denial)

## What's NOT verified (needs a phone)

- Real FCM banner emission and deep-link tap handling
- POST_NOTIFICATIONS first-launch prompt UX
- Current-screen suppression end-to-end (silent refresh vs banner)
- Master push toggle suppressing real banners while still writing inbox rows
- Scheduler firing `TASK_DUE_SOON` against a real task

## Suggested manual e2e (when you have two devices)

1. Sign in as A on device 1, as B on device 2. Wait for both FCM tokens to register (`POST /devices/fcm-token`).
2. Set `app.firebase.service-account-path` env var on the backend so `PushService.enabled = true`.
3. A invites B by email → B's device should banner with title "Group invitation"; tap → Invitations screen.
4. B accepts → A's device should banner "Invitation accepted".
5. A creates a group task assigned to B → B's device banners "Task assigned".
6. B opens that task in `GroupTaskDetail`. A reassigns to themselves → no banner on B (suppression), task list refreshes silently.
7. B toggles "Push notifications" off in Settings → A creates another assignment → B's inbox grows (badge increments) but no banner.
8. Seed a task with `dueDate ≈ now + 10 min` → within 5 min the scheduled job fires `TASK_DUE_SOON` and the assignee banners once; subsequent ticks don't re-fire.

---

## Out-of-scope deferred (in plan doc)

- Room cache for inbox/invitations (StateFlow + 30s TTL is shipped instead)
- Per-type push preferences, quiet hours, grouped notifications
- Invitation expiry job
- Backend-localized notification content (today client renders title/body verbatim)
