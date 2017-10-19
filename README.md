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


### Quick Start (from source code)

* Add modules leafdb-core and connector (leafdb-jdbc or leafdb-android) to your project
* Append `':leafdb-jdbc', ':leafdb-core'` or `':leafdb-android', ':leafdb-core'` to `settings.gradle`
* Add module connector dependency to your project (e.g. `compile project(path: ':leafdb-jdbc')`)
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
* Now you are ready to use it

### Field
Could represent inner field of document, document itself (`Field()`) or calculated value
 
```kotlin
val A = Field("a")
val B = Field("b")
val C = A + B
val rootDocument = Field()
```


### DSL example for fields and conditions
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
//Synchronous call
val list = leafDb
        .select("SampleTable")
        .findAs(SampleTable::class.java)
        .execute()

//Async call with Rx
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
     
```
Unsubscribe from updates and close connection:  
`subscribtion.dispose() ` 

### Aggregation query
```kotlin
val avg = leafDb.aggregate("SampleTable", ID, AggregateFunction.AVG).execute()
```

### Insert query  

* Insert Json
```kotlin
leafDb.insert("SampleTable", "{\"id\" : 5, \"value\" : \"sample\" }").execute()
```

* Insert Object
```kotlin
data class Data(val id : Int, val value : String)
leafDb.insert("SampleTable", Data(5, "Sample")).execute()
```

* Insert Object extracted from wrapper (works either for objects or json strings).
Parameter `innerDocument` specifies to path to desired document in parent json string or object
```kotlin
data class Wrapper(val data : Data, val meta : Meta)
leafDb.insert("SampleTable", wrapperInstance, Field("data")).execute()
```

* The last optional parameter of all insert/update function is `notifySubscribers`.
If `true`, all subscribed select queries will receive callback. If `false`, change notification will not be received.

```kotlin
leafDb.insert("SampleTable", "{\"id\" : 5, \"value\" : \"sample\" }", notifySubscribers = false).execute()
```

### Update query  

Fully rewrites document

#### Conditional update:
```kotlin
fun update(table: String, document: Any, condition: Condition?, innerDocument: Field? = null, notifySubscribers: Boolean = true)
```
If `condition` is `null`, all documents will be rewrited

```kotlin
leafDb.update("Sample Table", "{\"id\" : 5, \"value\" : \"sample\" }", Field("id") lessThanOrEqual  5).execute()
```

#### Primary key update

```kotlin
fun update(table: String, document: Any, pKey: Field, innerDocument: Field? = null, notifySubscribers: Boolean = true)
```
Example: specify `id` field as primary key and update entry with field id=5

```kotlin
leafDb.update("Sample Table", "{\"id\" : 5, \"value\" : \"sample\" }", Field("id")).execute()
```

#### Insert or update

```kotlin
fun insertOrUpdate(table: String, document: Any, pKey: Field, innerDocument: Field? = null, notifySubscribers: Boolean = true)
```
Works similar as primary key update, but creates new document if primary key does not exists in the able


### Partial update query  
```kotlin
fun partialUpdate(table: String, document: Any, condition: Condition?, innerDocument: Field? = null, notifySubscribers: Boolean = true, nullMergeStrategy: NullMergeStrategy = NullMergeStrategy.TAKE_DESTINATION)
fun partialUpdate(table: String, document: Any, pKey: Field, innerDocument: Field? = null, notifySubscribers: Boolean = true, nullMergeStrategy: NullMergeStrategy = NullMergeStrategy.TAKE_DESTINATION)
```
Merge old and new versions of document. Fields with similar names will be updated.
New fields will be added. If field exists in old document and is absent in new document, it will be kept as is.
Arrays will be rewritten, inner objects will be merged recursively.

Parameter `nullMergeStrategy` specifies how to handle situations when one document has null field, but another document stores something in the same field. 

Example:
```kotlin
data class Data(val id : Int, val value : String)
val ID = Field("id")

leafDb.partialUpdate("Sample Table", mapOf("id" to 1, "t" to 4), ID).execute()

leafDb.partialUpdate("Sample Table", mapOf("t" to 4), ID equal 5).execute()
```
This will add integer field "t" with value = 4  to documents with ids 4 and 5. `value` field will be kept as is.


### Bulk update 
* insertAll
* insertOrUpdateAll
* updateAll
* partialUpdateAll

Signatures:
```kotlin
(table: String, documents: Iterable<T>, predicate: (T) -> Condition?, innerDocument: Field? = null, notifySubscribers: Boolean = true)
```
Iterates all docs, finds update predicate for each doc and calls single update function for this doc with calculated condition.  

```kotlin
(table: String, documents: Any, pKey: Field, pathToArray: Field = Field(), innerDocument: Field? = null, notifySubscribers: Boolean = true)
```
Looks for json array in `documents` (could be json string or any object/list) and calls single update function for each item

`partialUpdateAll` also includes `nullMergeStrategy` as all partial update queries

### Deletion query  
```kotlin
fun delete(table: String, condition: Condition? = null, notifySubscribers: Boolean = true)

//deletes all from table
leafDb.delete("Sample Table").execute()


leafDb.delete("Sample Table", ID greaterThan 5).execute()
```


### Other functions
* `count` finds count of documents in the table with or without condition
* `engineVersion` returns backend version
* `random` returns random value
* `isJson` checks if parameter string is valid json
* `getInnerDocument` extracts inner document from json string
* `getInnerArray` extracts inner array from json string

## Java 7

```java
leafDb.updateAll("SampleTable", new LinkedList<SampleData>() {{
          add(new SampleData(3, 3));
      }}, new Function1<SampleData, Condition>() {
          @Override
          public Condition invoke(SampleData sampleData) {
              return A.equal(sampleData.a);
          }
      }).execute();

      final List<SampleData> dbData = leafDb.select("SampleTable").where(A.plus(B).greaterThan(5)).findAs(SampleData.class).execute();

```