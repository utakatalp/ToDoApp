---
name: add-api-endpoint
description: Wire a new Retrofit endpoint through every layer of the ToDoApp — request/response DTO, ToDoApi.kt, RemoteDataSource interface+impl, Repository interface+impl, and RepositoryModule binding if the repo is new. Invoke when the user says "add an API endpoint", "wire a new backend call", or "add POST /foo/bar".
---

# add-api-endpoint — End-to-End Endpoint Scaffolding

## Inputs to collect

1. **HTTP verb** — GET/POST/PUT/DELETE.
2. **Path** — e.g. `groups/{groupId}/archive`. Relative to `BuildConfig.BASE_URL` — do NOT include a host.
3. **Path/query params** — names + types.
4. **Request body** (if POST/PUT) — field names + types.
5. **Response body** — field names + types. If the backend wraps in `BaseResponse<T>`, the concrete type is the `T`.
6. **Repository to attach to** — existing (e.g. `GroupRepository`) or new. If new, collect the name.
7. **Method name** — camelCase on the API interface, e.g. `archiveGroup`.

## Layers touched (in order)

| # | Layer | File |
|---|-------|------|
| 1 | Request DTO (if body) | `app/src/main/java/com/todoapp/mobile/data/model/network/request/<Name>Request.kt` |
| 2 | Response DTO (if new shape) | `app/src/main/java/com/todoapp/mobile/data/model/network/data/<Name>Data.kt` |
| 3 | Retrofit API | `app/src/main/java/com/todoapp/mobile/data/source/remote/api/ToDoApi.kt` (append method) |
| 4 | RemoteDataSource interface | `app/src/main/java/com/todoapp/mobile/data/source/remote/datasource/<Repo>RemoteDataSource.kt` |
| 5 | RemoteDataSource impl | Same folder, `<Repo>RemoteDataSourceImpl.kt` |
| 6 | Domain repository interface | `app/src/main/java/com/todoapp/mobile/domain/repository/<Repo>Repository.kt` |
| 7 | Data repository impl | `app/src/main/java/com/todoapp/mobile/data/repository/<Repo>RepositoryImpl.kt` |
| 8 | DI binding (only if repo is NEW) | `app/src/main/java/com/todoapp/mobile/di/RepositoryModule.kt` |

## Templates

### Request DTO
```kotlin
package com.todoapp.mobile.data.model.network.request

import kotlinx.serialization.Serializable

@Serializable
data class <Name>Request(
    val field1: String,
    val field2: Long? = null,
)
```

### Response DTO
```kotlin
package com.todoapp.mobile.data.model.network.data

import kotlinx.serialization.Serializable

@Serializable
data class <Name>Data(
    val id: Long,
    val name: String,
)
```

### ToDoApi.kt method
```kotlin
@POST("groups/{groupId}/archive")
suspend fun archiveGroup(
    @Path("groupId") groupId: Long,
    @Body request: ArchiveGroupRequest,
): Response<BaseResponse<ArchiveGroupData>>
```
Convention: all methods return `Response<BaseResponse<T>>`. Path uses `{braces}` for `@Path`; query params via `@Query`.

### RemoteDataSource interface + impl
```kotlin
// interface
suspend fun archiveGroup(groupId: Long, request: ArchiveGroupRequest): Result<ArchiveGroupData>

// impl — follow the existing pattern: call api, unwrap BaseResponse, map to Result.
override suspend fun archiveGroup(groupId: Long, request: ArchiveGroupRequest): Result<ArchiveGroupData> =
    runCatching {
        val response = api.archiveGroup(groupId, request)
        val body = response.body() ?: error("Empty response")
        body.data ?: error(body.message ?: "Unknown error")
    }
```

### Repository interface + impl
```kotlin
// domain interface
suspend fun archiveGroup(groupId: Long): Result<Group>

// data impl — map DTO → domain model, update Room cache if applicable.
override suspend fun archiveGroup(groupId: Long): Result<Group> =
    remoteDataSource.archiveGroup(groupId, ArchiveGroupRequest(...))
        .map { it.toDomain() }
        .onSuccess { groupDao.upsert(it.toEntity()) }
```

### RepositoryModule binding (NEW repo only)
```kotlin
@Binds
@Singleton
abstract fun bindArchiveRepository(impl: ArchiveRepositoryImpl): ArchiveRepository
```

## Non-negotiable rules

- **Never hardcode the host.** All Retrofit paths are relative; the base URL comes from `BuildConfig.BASE_URL` (wired in `NetworkModule`). See CLAUDE.md §Networking.
- **All request/response DTOs are `@Serializable`.** Project uses kotlinx.serialization, not Moshi/Gson.
- **All responses wrap in `BaseResponse<T>`.** The method returns `Response<BaseResponse<T>>` — do not flatten at the Retrofit layer.
- **Auth is automatic.** The OkHttpClient has an `AuthInterceptor` — do NOT add `@Header("Authorization")` parameters.
- **401 refresh is automatic** via `TokenRefreshAuthenticator`. Don't handle it per-call.
- **Domain layer never imports `retrofit2.*` or DTO classes.** Repository interface is in `domain/`, impl in `data/`, and only the impl touches DTOs.
- **If the call updates persisted state (tasks, groups), update the Room cache in the RepositoryImpl** after a successful network response, following the existing `TaskRepositoryImpl` / `GroupRepositoryImpl` pattern.

## Verification

1. `./gradlew :app:ktlintCheck :app:detekt` — must pass.
2. `./gradlew :app:assembleDebug` — must build (KSP regens Hilt graph).
3. Test the call from a ViewModel action → repository method → observe state change.
4. If the endpoint is authed, verify the Bearer token is attached by enabling OkHttp logging at DEBUG.
