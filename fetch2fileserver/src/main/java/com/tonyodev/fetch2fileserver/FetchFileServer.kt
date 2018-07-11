package com.tonyodev.fetch2fileserver

import android.content.Context
import com.tonyodev.fetch2core.FileResource
import com.tonyodev.fetch2core.Func
import java.net.ServerSocket

/** A lightweight TCP File Server that acts like an HTTP file server
 * designed specifically for Android to distribute files from on device to another.
 * The Fetch File Server works great with Fetch and the FetchFileResourceDownloader to download file resources.*/
interface FetchFileServer {

    /** File Server Unique id.*/
    val id: String

    /** The port the File server is listening to file requests on.
     * The port id is available after the server has started.
     * */
    val port: Int

    /** The file server IP address. The IP address is available after the server has started. */
    val address: String

    /** Indicates if the File Server is shutdown.*/
    val isShutDown: Boolean

    /** Starts the file server. File Server beings to listen for requests on the designated port.*/
    fun start()

    /** Shuts down the file server. The File server will no longer accept request or allow
     * file resources to be attached. Once called any method call to the instance will throw
     * an exception indicating that the instance is no longer available for use.
     * @param forced forces the file server to cancel active requests that are being served to
     * any connected clients and terminates all operations and connections.
     * */
    fun shutDown(forced: Boolean = false)

    /** Adds a FileResource that can be served to requesting clients.
     * @param fileResource a file resource.
     * */
    fun addFileResource(fileResource: FileResource)

    /** Adds a list of FileResource that can be served to requesting clients.
     * @param fileResources a list file resource.
     * */
    fun addFileResources(fileResources: Collection<FileResource>)

    /** Removes a fileResource from the instance of the File Server.
     *@param fileResource file resource
     * */
    fun removeFileResource(fileResource: FileResource)

    /** Removes a list of FileResource from the File Server instance.
     * @param fileResources a list file resource.
     * */
    fun removeFileResources(fileResources: Collection<FileResource>)

    /** Removes all FileResources managed by this instance.*/
    fun removeAllFileResources()

    /** Checks if a File Resource is managed by this instance.
     * @param fileResourceId file resource id
     * @param func callback the result will be returned on. True if the file resource
     * is being managed. False otherwise.
     * */
    fun containsFileResource(fileResourceId: Long, func: Func<Boolean>)

    /** Gets a list of all File Resources managed by this File Server instance.
     * @param func callback the result is returned on.
     * */
    fun getFileResources(func: Func<List<FileResource>>)

    /** Gets the Catalog(All File Resources) managed by this File Server instances
     * as JSON. The FileResources `file` field is excluded.
     * @param func callback the result will be returned on.
     * */
    fun getCatalog(func: Func<String>)

    /** Queries the File Server instance for a managed file resource if it exist.
     * @param fileResourceId file resource id
     * @param func callback the result will be returned on. Result maybe null.
     * */
    fun getFileResource(fileResourceId: Long, func: Func<FileResource?>)

    /** Creates an instance of FetchFileServer.*/
    class Builder(
            /** context*/
            private val context: Context) {

        private var serverSocket = ServerSocket(0)
        private var clearDatabaseOnShutdown = false
        private var logger = FetchFileServerLogger()
        private var fileServerAuthenticator: FetchFileServerAuthenticator? = null
        private var fileServerDelegate: FetchFileServerDelegate? = null
        private var transferListener: FetchTransferListener? = null
        private var fileResourceDatabaseName = "LibFetchFileServerDatabaseLib.db"

        /** Set Custom Server Socket
         * @param serverSocket
         * @return builder
         * */
        fun setServerSocket(serverSocket: ServerSocket): Builder {
            this.serverSocket = serverSocket
            return this
        }

        /** Clears the File Server FileResources database on shut down
         * @param clear Default is false
         * @return builder
         * */
        fun setClearDatabaseOnShutdown(clear: Boolean): Builder {
            this.clearDatabaseOnShutdown = clear
            return this
        }

        /** Set File Server Logger.
         * @param logger Default. Uses the default logger which is enabled
         * @return builder
         * */
        fun setLogger(logger: FetchFileServerLogger): Builder {
            this.logger = logger
            return this
        }

        /** Set authenticator object.
         *@param fetchFileServerAuthenticator Default is null. If Null, the file server will
         * accept all requests.
         * @return builder
         * */
        fun setAuthenticator(fetchFileServerAuthenticator: FetchFileServerAuthenticator): Builder {
            this.fileServerAuthenticator = fetchFileServerAuthenticator
            return this
        }

        /** Set Delegate
         * @param fetchFileServerDelegate Default is null. If Null, the file server will
         * attempt to handle custom requests and actions.
         * @return builder
         * */
        fun setDelegate(fetchFileServerDelegate: FetchFileServerDelegate): Builder {
            this.fileServerDelegate = fetchFileServerDelegate
            return this
        }

        /** Set Transfer Listener
         * @param fetchTransferListener Default is null.
         * @return builder
         * */
        fun setTransferListener(fetchTransferListener: FetchTransferListener): Builder {
            this.transferListener = fetchTransferListener
            return this
        }

        /** Set default Database name the File Server instance will use. This is used to store
         * FileResource information that can later be retrieved by the File Server.
         * @param databaseName Default is LibFetchFileServerDatabaseLib.db
         * @return builder
         * */
        fun setFileServerDatabaseName(databaseName: String): Builder {
            if (databaseName.isNotEmpty()) {
                this.fileResourceDatabaseName = databaseName
            }
            return this
        }

        /** Build the FetchFileServer Instance.
         * @return new Fetch File Server instance.
         * */
        fun build(): FetchFileServer {
            return FetchFileServerImpl(context = context.applicationContext,
                    serverSocket = serverSocket,
                    clearFileResourcesDatabaseOnShutdown = clearDatabaseOnShutdown,
                    logger = logger,
                    databaseName = fileResourceDatabaseName,
                    fetchFileServerAuthenticator = fileServerAuthenticator,
                    fetchFileServerDelegate = fileServerDelegate,
                    fetchTransferListener = transferListener)
        }

    }

}