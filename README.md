# Bhandar : Simple network/local source repository

[![Kotlin Alpha](https://kotl.in/badges/alpha.svg)](https://kotlinlang.org/docs/components-stability.html)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-purple.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-CC_BY_SA_4.0-blue.svg)](https://github.com/bidrohi/seahorse/blob/master/LICENSE.md)

![badge-jvm](http://img.shields.io/badge/platform-jvm-DB413D.svg?style=flat)
![badge-android](http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat)
![badge-ios](http://img.shields.io/badge/platform-ios-CDCDCD.svg?style=flat)
![badge-watchos](http://img.shields.io/badge/platform-watchos-C0C0C0.svg?style=flat)
![badge-tvos](http://img.shields.io/badge/platform-tvos-808080.svg?style=flat)
![badge-js](http://img.shields.io/badge/platform-js-DB413D.svg?style=flat)
![badge-wasm](http://img.shields.io/badge/platform-wasm-DB413D.svg?style=flat)
![badge-mac](http://img.shields.io/badge/platform-macos-111111.svg?style=flat)
![badge-linux](http://img.shields.io/badge/platform-linux-2D3F6C.svg?style=flat)
![badge-windows](http://img.shields.io/badge/platform-windows-4D76CD.svg?style=flat)

Bhandar (ভান্ডার) is a simple repository implementation that can fetch data from a fetching source and (if configured) a store for cached access.

The repository using only one model for simplicity, so the implementation on the sources would need to convert to the shared model definition.

## Gradle setup

<details>
<summary>Kotlin DSL</summary>

```kotlin
implementation("com.bidyut.tech.bhandar:bhandar:<version>")
```

</details>
<details open>
<summary>Version Catalogue</summary>

```toml
[versions]
bhandar = "version"

[libraries]
bhandar = { group = "com.bidyut.tech.bhandar", name = "bhandar", version.ref = "bhandar" }
```

</details>

## Configuring a repository

To create a new repository, we need to configure a fetcher and a storage. Let us assume a simple data model and request.
```kotlin
data class Request(
    val id: String,
)

data class DataModel(
    val id: String,
    val value: String,
)
```
 
So we need a fetcher like
```kotlin
val fetcher = DataFetcher.of<Request, DataModel> {
    // return Result.success() or Result.failure() based on the response
}
```
 
and a storage like
```kotlin
val storage = Storage.of<Request, DataModel>(
    read = { request -> 
        // return flow with the data or null
    },
    write = { request, newValue ->
        // write the data to the storage
    },
)
```

Optionally both the fetcher and storage can have a different validation check; by default it is just non-null.
