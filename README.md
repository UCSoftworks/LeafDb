# LeafDb
NoSQL database for Android and Java written in Kotlin

## Features

* Document-oriented database powered by SQL Lite 3.19+
* Supports search indexes for document fields
* Supports search inside documents
* Supports Kotlin (1.1.4-2+), Java (1.7+), Android (API v9+)
* Supports inserting either raw json's or objects/lists
* Dynamic object mapping
* Feature-rich DSL for querying (Kotlin only)
* Partial update: append only diff between new and existing document
* Optional Rx 2.0 support for async or reactive queries
* Modular architecture: LeafDb Core does not include connectors and drivers


### Quick Start

* Subclass LeafDbHelper (From LeafDB-jdbc or LeafDb-android)
```kotlin
class SampleHelper(dbName: String, version: Int) : LeafDbJdbcHelper(dbName, version)
```
* Implement onCreate and onUpdate methods
```kotlin
    override fun onCreate(): LeafDbSchemaMigration =
            LeafDbSchemaMigration()
                    .createTable("SampleTable")
                    .createIndex("SampleTable", "sample_index", Field("path", "to", "field"))
```
* When you need to add or remove tables or search indexes, you should increase db version number (`version: Int`) and add migration into your helper
* Create instance of your helper, take leafDb instance from it
```kotlin
val leafDbJdbcHelper = SampleHelper("sample.db", 3) 
val leafDb = leafDbJdbcHelper.leafDb
```
* Start querying 

### DSL example
```kotlin
val A = Field("a")
val B = Field("b")
val C = Field("c")
val D = Field("d")
    
val condition1 = A greaterThan 0
val condition2 = A + C equal 15 and !(B equal "Str") or (C notEqual 21)
val condition3 = D between Date()..Date()
val condition4 = E between 1..15
val condition4 = Field("child", "a") % 5 lessThanOrEqual 2
val condition5 = B like "__asd%"
val condition5 = C.isNotNull()
```
There is a full set of arithmetic, logic and comparision operators plus string functions for working with fields.

### Select query
```kotlin
    
    val list = leafDb
            .select("SampleTable")
            .findAs(SampleTable::class.java)
            .execute()
    
     val subscribtion = leafDb
             .select("SampleTable")
             .distinct()
             .field(Field("path", "to", "required", "inner", "doc"))
             .orderBy(A)
             .offset(10)
             .limit(10)
             .where(A + C equal 15 and !(B equal "Str") or (C notEqual 21))
             .findAs(SampleTable::class.java)
             .asFlowable()
             .subscribe({}, {}, {})
             .dispose()
```
