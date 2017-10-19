package com.ucsoftworks.leafdb

import com.ucsoftworks.leafdb.dsl.AggregateFunction
import com.ucsoftworks.leafdb.dsl.Condition
import com.ucsoftworks.leafdb.dsl.Field
import com.ucsoftworks.leafdb.querying.*
import com.ucsoftworks.leafdb.serializer.NullMergeStrategy
import com.ucsoftworks.leafdb.serializer.Serializer
import com.ucsoftworks.leafdb.wrapper.ILeafDbProvider
import querying.SelectQueryBuilder

/**
 * Created by Pasenchuk Victor on 11/08/2017
 */
class LeafDb(internal val leafDbProvider: ILeafDbProvider) {

    private val serializer = Serializer()

    fun random() = LeafDbStringQuery(getRandomQuery(), leafDbProvider).map(Long::class.java)

    fun engineVersion() = LeafDbStringQuery(getEngineVersionQuery(), leafDbProvider)

    fun isJson(doc: String): Boolean {
        val valid = LeafDbStringQuery(getJsonValidQuery(doc), leafDbProvider).execute()
        if (valid == "1")
            return true
        return false
    }

    fun getInnerDocument(json: String, path: Field) =
            LeafDbStringQuery(getInnerDocumentSql(path, json), leafDbProvider)


    fun getInnerArray(json: String, path: Field) =
            LeafDbStringListQuery(getInnerArrayQuery(path, json), leafDbProvider)

    fun select(table: String) = SelectQueryBuilder(table, leafDbProvider)

    fun count(table: String) = count(table, null)
    fun count(table: String, condition: Condition? = null) = LeafDbStringQuery(getCountQuery(table, condition), leafDbProvider).map(Long::class.java)

    fun aggregate(table: String, field: Field, aggregateFunction: AggregateFunction) = aggregate(table, field, aggregateFunction, null)
    fun aggregate(table: String, field: Field, aggregateFunction: AggregateFunction, condition: Condition? = null) =
            LeafDbStringQuery(getAggregationQuery(aggregateFunction, field, table, condition), leafDbProvider).map(Double::class.java)

    fun <T> aggregate(table: String, field: Field, aggregateFunction: AggregateFunction, clazz: Class<T>) = aggregate(table, field, aggregateFunction, clazz, null)
    fun <T> aggregate(table: String, field: Field, aggregateFunction: AggregateFunction, clazz: Class<T>, condition: Condition? = null) =
            LeafDbStringQuery(getAggregationQuery(aggregateFunction, field, table, condition), leafDbProvider).map(clazz)

    fun delete(table: String) = delete(table, null, true)
    fun delete(table: String, condition: Condition? = null, notifySubscribers: Boolean = true): ILeafDbModificationQuery = LeafDbModificationQuery(table, getDeleteQuery(table, condition), leafDbProvider, notifySubscribers)


    fun insert(table: String, document: Any) = insert(table, document, null, true)
    fun insert(table: String, document: Any, innerDocument: Field? = null, notifySubscribers: Boolean = true): ILeafDbModificationQuery =
            entryUpdate(table, document, innerDocument, notifySubscribers, this::getInsertSql)


    private fun entryUpdate(table: String, document: Any, innerDocument: Field? = null, notifySubscribers: Boolean, sqlGenerator: (table: String, doc: String) -> String): ILeafDbModificationQuery {
        return LeafDbModificationBlockQuery {
            val doc = getDoc(document, innerDocument)
            LeafDbModificationQuery(table, sqlGenerator(table, doc), leafDbProvider, notifySubscribers).execute()
        }
    }

    private fun getDoc(document: Any, innerDocument: Field?): String {
        var doc = if (document is String && isJson(document)) document else serializer.getJsonFromObject(document)
        if (innerDocument != null)
            doc = getInnerDocument(doc, innerDocument).execute()!!
        return doc
    }

    fun update(table: String, document: Any, condition: Condition?) = update(table, document, condition, null, true)
    fun update(table: String, document: Any, condition: Condition?, innerDocument: Field? = null, notifySubscribers: Boolean = true): ILeafDbModificationQuery =
            entryUpdate(table, document, innerDocument, notifySubscribers) { t, d -> getUpdateSql(t, d, condition) }

    //todo generalize

    fun update(table: String, document: Any, pKey: Field) = update(table, document, pKey, null, true)
    fun update(table: String, document: Any, pKey: Field, innerDocument: Field? = null, notifySubscribers: Boolean = true): ILeafDbModificationQuery =
            entryUpdate(table, document, innerDocument, notifySubscribers) { t, d -> getUpdateSql(t, d, getInnerDocConditionSql(pKey, document, innerDocument)) }

    fun insertOrUpdate(table: String, document: Any, pKey: Field) = insertOrUpdate(table, document, pKey, null, true)
    fun insertOrUpdate(table: String, document: Any, pKey: Field, innerDocument: Field? = null, notifySubscribers: Boolean = true): ILeafDbModificationQuery =
            LeafDbModificationBlockQuery {
                val count = LeafDbStringQuery(getDocCountQuery(table, pKey, document, innerDocument), leafDbProvider).map(Long::class.java).execute()!!
                if (count == 0L)
                    entryUpdate(table, document, innerDocument, notifySubscribers, this::getInsertSql).execute()
                else
                    entryUpdate(table, document, innerDocument, notifySubscribers) { t, d -> getUpdateSql(t, d, getInnerDocConditionSql(pKey, document, innerDocument)) }.execute()
            }

    //todo generalize

    fun partialUpdate(table: String, document: Any, condition: Condition) = partialUpdate(table, document, condition, null, true)
    fun partialUpdate(table: String, document: Any, condition: Condition?, innerDocument: Field? = null, notifySubscribers: Boolean = true, nullMergeStrategy: NullMergeStrategy = NullMergeStrategy.TAKE_DESTINATION): ILeafDbModificationQuery =
            LeafDbModificationBlockQuery {
                conditionalPartialUpdate(table, document, condition, innerDocument, notifySubscribers, nullMergeStrategy)
            }

    fun partialUpdate(table: String, document: Any, pKey: Field) = partialUpdate(table, document, pKey, null, true)
    fun partialUpdate(table: String, document: Any, pKey: Field, innerDocument: Field? = null, notifySubscribers: Boolean = true, nullMergeStrategy: NullMergeStrategy = NullMergeStrategy.TAKE_DESTINATION): ILeafDbModificationQuery =
            LeafDbModificationBlockQuery {
                pKeyPartialUpdate(table, document, pKey, innerDocument, notifySubscribers, nullMergeStrategy)
            }

    fun insertAll(table: String, documents: Any, pathToArray: Field = Field()) = insertAll(table, documents, pathToArray, null, true)
    fun insertAll(table: String, documents: Any, pathToArray: Field = Field(), innerDocument: Field? = null, notifySubscribers: Boolean = true): ILeafDbModificationQuery =
            bulkUpdate(table,
                    extractDocuments(documents, pathToArray),
                    { insert(table, it, innerDocument, false).execute() },
                    notifySubscribers)

    fun insertOrUpdateAll(table: String, documents: Any, pKey: Field) = insertOrUpdateAll(table, documents, pKey, Field(), null, true)
    fun insertOrUpdateAll(table: String, documents: Any, pKey: Field, pathToArray: Field = Field(), innerDocument: Field? = null, notifySubscribers: Boolean = true): ILeafDbModificationQuery =
            bulkUpdate(table,
                    extractDocuments(documents, pathToArray),
                    { insertOrUpdate(table, it, pKey, innerDocument, false).execute() },
                    notifySubscribers)

    fun updateAll(table: String, documents: Any, pKey: Field) = updateAll(table, documents, pKey, Field(), null, true)
    fun updateAll(table: String, documents: Any, pKey: Field, pathToArray: Field = Field(), innerDocument: Field? = null, notifySubscribers: Boolean = true): ILeafDbModificationQuery =
            bulkUpdate(table,
                    extractDocuments(documents, pathToArray),
                    { update(table, it, pKey, innerDocument, false).execute() },
                    notifySubscribers)


    fun <T : Any> updateAll(table: String, documents: Iterable<T>, predicate: (T) -> Condition?) = updateAll(table, documents, predicate, null, true)
    fun <T : Any> updateAll(table: String, documents: Iterable<T>, predicate: (T) -> Condition?, innerDocument: Field? = null, notifySubscribers: Boolean = true): ILeafDbModificationQuery =
            bulkUpdate(table,
                    { documents },
                    { update(table, it, predicate(it), innerDocument, false).execute() },
                    notifySubscribers)

    fun partialUpdateAll(table: String, documents: Any, pKey: Field) = partialUpdateAll(table, documents, pKey, Field(), null, true)
    fun partialUpdateAll(table: String, documents: Any, pKey: Field, pathToArray: Field = Field(), innerDocument: Field? = null, notifySubscribers: Boolean = true, nullMergeStrategy: NullMergeStrategy = NullMergeStrategy.TAKE_DESTINATION): ILeafDbModificationQuery =
            bulkUpdate(table,
                    extractDocuments(documents, pathToArray),
                    { partialUpdate(table, it, pKey, innerDocument, false, nullMergeStrategy).execute() },
                    notifySubscribers)

    fun <T : Any> partialUpdateAll(table: String, documents: Iterable<T>, predicate: (T) -> Condition?) = partialUpdateAll(table, documents, predicate, null, true)
    fun <T : Any> partialUpdateAll(table: String, documents: Iterable<T>, predicate: (T) -> Condition?, innerDocument: Field? = null, notifySubscribers: Boolean = true, nullMergeStrategy: NullMergeStrategy = NullMergeStrategy.TAKE_DESTINATION): ILeafDbModificationQuery =
            bulkUpdate(table,
                    { documents },
                    { partialUpdate(table, it, predicate(it), innerDocument, false, nullMergeStrategy).execute() },
                    notifySubscribers)

    private fun pKeyPartialUpdate(table: String, document: Any, pKey: Field, innerDocument: Field?, notifySubscribers: Boolean = true, nullMergeStrategy: NullMergeStrategy = NullMergeStrategy.TAKE_DESTINATION) {
        val query = getInnerDocConditionSql(pKey, document, innerDocument)
        doPartialUpdate(query, table, document, innerDocument, notifySubscribers, nullMergeStrategy)
    }

    private fun conditionalPartialUpdate(table: String, document: Any, condition: Condition?, innerDocument: Field?, notifySubscribers: Boolean = true, nullMergeStrategy: NullMergeStrategy = NullMergeStrategy.TAKE_DESTINATION) {
        val query = getIndexedDocsQuery(table, condition)
        doPartialUpdate(query, table, document, innerDocument, notifySubscribers, nullMergeStrategy)
    }

    private fun doPartialUpdate(query: String, table: String, document: Any, innerDocument: Field?, notifySubscribers: Boolean, nullMergeStrategy: NullMergeStrategy) {
        val readableDb = leafDbProvider.readableDb
        val originals = readableDb.selectQuery(query).collectIndexedStrings(2)
        readableDb.close()
        originals.forEach { (first, second) ->
            entryUpdate(table, document, innerDocument, notifySubscribers) { t, d ->
                getUpdateByIdSql(t, serializer.merge(d, second, nullMergeStrategy), first)
            }.execute()
        }
    }

    private fun extractDocuments(documents: Any, pathToArray: Field) =
            { if (documents is String && isJson(documents)) getInnerArray(documents, pathToArray).execute() else getInnerArray(serializer.getJsonFromObject(documents), pathToArray).execute() }


    private fun <T : Any> bulkUpdate(table: String, docProvider: () -> Iterable<T>, queryRunner: (doc: T) -> Unit, notifySubscribers: Boolean = true) =
            LeafDbModificationBlockQuery {
                val docs = docProvider()
                docs.forEach(queryRunner)
                if (notifySubscribers)
                    leafDbProvider.notifySubscribers(table)
            }

//todo
//    fun getDocCountQuery(table: String, pKey: Field, document: Any, innerDocument: Field?) =
//            "SELECT COUNT(*) FROM $table ${getInnerDocConditionSql(pKey, document, innerDocument)};"
//
//    fun getInnerDocConditionSql(pKey: Field, document: Any, innerDocument: Field?) =
//            "WHERE $pKey = (${getExtractSql(pKey, getDoc(document, innerDocument))})"
//
//    fun getInnerArrayQuery(path: Field, json: String) =
//            "SELECT value FROM (SELECT json_each($path) FROM (SELECT ${json.sqlValue} as doc));"

    private fun getRandomQuery() = "SELECT random();"

    private fun getEngineVersionQuery() = "SELECT sqlite_version();"

    private fun getJsonValidQuery(doc: String) = "SELECT json_valid(${doc.sqlValue});"

    private fun getInnerDocumentSql(path: Field, json: String) =
            "${getExtractSql(path, json)};"

    private fun getDeleteQuery(table: String, condition: Condition?) =
            "DELETE FROM $table${getConditionBlock(condition)};"

    private fun getCountQuery(table: String, condition: Condition?) =
            "SELECT COUNT(*) FROM $table${getConditionBlock(condition)};"

    private fun getDocCountQuery(table: String, pKey: Field, document: Any, innerDocument: Field?) =
            "SELECT COUNT(*) FROM $table ${getInnerDocConditionSql(pKey, document, innerDocument)};"

    private fun getIndexedDocsQuery(table: String, condition: Condition?) =
            "SELECT id, doc FROM $table ${getConditionBlock(condition)};"

    private fun getInnerArrayQuery(path: Field, json: String) =
            "SELECT value FROM (SELECT json_each($path) FROM (SELECT ${json.sqlValue} as doc));"

    private fun getInsertSql(table: String, doc: String) =
            "INSERT into $table (timestamp, doc) values (strftime('%s','now'), ${doc.sqlValue});"

    private fun getUpdateSql(table: String, doc: String, condition: Condition? = null) =
            "UPDATE $table SET timestamp = strftime('%s','now'), doc = ${doc.sqlValue}${getConditionBlock(condition)};"

    private fun getUpdateByIdSql(table: String, doc: String, id: Long) =
            "UPDATE $table SET timestamp = strftime('%s','now'), doc = ${doc.sqlValue} WHERE id = $id;"

    private fun getUpdateSql(table: String, doc: String, conditionSql: String) =
            "UPDATE $table SET timestamp = strftime('%s','now'), doc = ${doc.sqlValue} $conditionSql;"

    private fun getAggregationQuery(aggregateFunction: AggregateFunction, field: Field, table: String, condition: Condition?) =
            "SELECT $aggregateFunction(f) FROM (SELECT $field AS f FROM $table${getConditionBlock(condition)});"


    //internal

    private fun getExtractSql(path: Field, json: String) =
            "SELECT $path FROM (SELECT ${json.sqlValue} as doc)"

    private fun getConditionBlock(condition: Condition?) =
            if (condition != null) " WHERE $condition" else ""


    private fun getInnerDocConditionSql(pKey: Field, document: Any, innerDocument: Field?) =
            "WHERE $pKey = (${getExtractSql(pKey, getDoc(document, innerDocument))})"

}
