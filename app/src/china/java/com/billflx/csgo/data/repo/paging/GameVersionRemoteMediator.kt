package com.billflx.csgo.data.repo.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import androidx.sqlite.db.SimpleSQLiteQuery
import coil.network.HttpException
import com.billflx.csgo.bean.CsRemoteVersionInfo
import com.billflx.csgo.data.db.CSVersionInfo
import com.billflx.csgo.data.db.CSVersionInfoDatabase
import com.billflx.csgo.data.mapper.toEntity
import com.billflx.csgo.data.net.AppUpdateApi
import com.billflx.csgo.data.repo.CSVersionInfoRepository
import com.gtastart.common.util.Coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class GameVersionRemoteMediator(
    private val db: CSVersionInfoDatabase,
    private val api: AppUpdateApi,
    private val repo: CSVersionInfoRepository
): RemoteMediator<Int, CSVersionInfo>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CSVersionInfo>
    ): MediatorResult {
        return try {
            val loadKey = 1 // 不分页
            val versions = api.getCsVersion()
            if (loadType == LoadType.REFRESH) {
                Log.d("", "load: 刷新load")
            }

            val entities = versions.map { it.toEntity() }
            db.withTransaction {
                repo.insertIfEmpty(db.getCSVersionInfoDao(), versions = entities)
            }

            MediatorResult.Success(
                endOfPaginationReached = true
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        } catch (e: Throwable) { // 保底，总感觉这可能会导致闪退
            MediatorResult.Error(e)
        }
    }

}