package com.joel.communication.builders

import androidx.paging.LoadType
import androidx.paging.PagingSource
import com.joel.communication.annotations.CommunicationsMarker
import com.joel.communication.models.PagingModel
import com.joel.communication.paging.NetworkPagingSource
import com.joel.communication.paging.RemoteAndLocalPagingSource
import com.joel.communication.valueclasses.Duration
import kotlinx.coroutines.CoroutineScope

@CommunicationsMarker
class PagingBuilder<T : PagingModel> @PublishedApi internal constructor() {

    /**
     * The page size to load at once from [PagingSource].
     *
     * Default is 10.
     */
    var defaultPageSize: Int = 10

    /**
     * Whether or not delete all the items when [LoadType] is [LoadType.REFRESH] state.
     *
     * Default is true.
     */
    var deleteOnRefresh: Boolean = true

    /**
     * Whether if the [PagingSource] comes only from api source.
     *
     * If true, it's calling the [NetworkPagingSource], otherwise, [RemoteAndLocalPagingSource].
     *
     * Default is false.
     */
    var onlyApiCall: Boolean = false

    /**
     * The name of the page query parameter on the request.
     */
    var pageQueryName = "page"

    /**
     * The time for the [RemoteAndLocalPagingSource] to refresh the data.
     *
     * After the time is reached, [RemoteAndLocalPagingSource] will call the api again and replace all the data.
     *
     * This has no effect if the [refresh] flag is true.
     */
    var cacheTimeout: Duration = Duration.hours(1)

    /**
     * Force [RemoteAndLocalPagingSource] to refresh when starting.
     *
     * This will update the updated timestamp on [PagingModel].
     *
     * When it is false, the [RemoteAndLocalPagingSource] will update when the last updated timestamp reach the [cacheTimeout].
     *
     * Default is false
     */
    var refresh: Boolean = false

    @PublishedApi
    internal var itemsDataSource:(() -> PagingSource<Int, T>)? = null

    @PublishedApi
    internal var lastUpdatedTimestamp: (suspend () -> Long?)? = null

    internal var deleteAll: ( suspend () -> Unit)? = null
    internal var insertAll: ( suspend (items: List<T>) -> Unit)? = null

    var cacheScope: CoroutineScope? = null

    /**
     * Call this to delete all data from local data source.
     *
     * <b>Warning:</b> this function is mandatory if [onlyApiCall] is false.
     */
    fun deleteAll(deleteAll: suspend () -> Unit) {
        this.deleteAll = deleteAll
    }

    /**
     * Call this function to insert all the data in the local data source that comes from api.
     *
     * <b>Warning:</b> this function is mandatory if [onlyApiCall] is false.
     */
    fun insertAll(onSuccess: suspend (items: List<T>) -> Unit) {
        this.insertAll = onSuccess
    }

    /**
     * Call this function to get items from local data source.
     *
     * <b>Warning:</b> this function is mandatory if [onlyApiCall] is false.
     */
    fun localSource(pagingSource: () -> PagingSource<Int, T>) {
        this.itemsDataSource = pagingSource
    }

    /**
     * Call this function to get the first item from local data source.
     * <b>Warning:</b> this functions is necessary for first loading propose.
     */
    fun firstItemDatabase(itemDatabase: suspend () -> T?) {
        lastUpdatedTimestamp = { itemDatabase()?.lastUpdatedTimestamp }
    }
}