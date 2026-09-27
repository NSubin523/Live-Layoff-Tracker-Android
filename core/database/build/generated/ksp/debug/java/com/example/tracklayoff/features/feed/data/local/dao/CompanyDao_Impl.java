package com.example.tracklayoff.features.feed.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.tracklayoff.features.feed.data.local.entity.CompanyEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CompanyDao_Impl implements CompanyDao {
  private final RoomDatabase __db;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  private final EntityUpsertionAdapter<CompanyEntity> __upsertionAdapterOfCompanyEntity;

  public CompanyDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE from companies";
        return _query;
      }
    };
    this.__upsertionAdapterOfCompanyEntity = new EntityUpsertionAdapter<CompanyEntity>(new EntityInsertionAdapter<CompanyEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `companies` (`id`,`company_name`,`impact_count`,`layoff_status`,`industry`,`location`,`reported_at`,`logo_url`,`trend_direction`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CompanyEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getCompanyName());
        if (entity.getImpactCount() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getImpactCount());
        }
        statement.bindString(4, entity.getLayoffStatus());
        statement.bindString(5, entity.getIndustry());
        statement.bindString(6, entity.getLocation());
        statement.bindLong(7, entity.getReportedAt());
        if (entity.getLogoUrl() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getLogoUrl());
        }
        statement.bindString(9, entity.getTrendDirection());
      }
    }, new EntityDeletionOrUpdateAdapter<CompanyEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `companies` SET `id` = ?,`company_name` = ?,`impact_count` = ?,`layoff_status` = ?,`industry` = ?,`location` = ?,`reported_at` = ?,`logo_url` = ?,`trend_direction` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CompanyEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getCompanyName());
        if (entity.getImpactCount() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getImpactCount());
        }
        statement.bindString(4, entity.getLayoffStatus());
        statement.bindString(5, entity.getIndustry());
        statement.bindString(6, entity.getLocation());
        statement.bindLong(7, entity.getReportedAt());
        if (entity.getLogoUrl() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getLogoUrl());
        }
        statement.bindString(9, entity.getTrendDirection());
        statement.bindString(10, entity.getId());
      }
    });
  }

  @Override
  public Object clearAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertCompanies(final List<CompanyEntity> companies,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfCompanyEntity.upsert(companies);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CompanyEntity>> observeCompanies() {
    final String _sql = "SELECT * FROM companies ORDER BY reported_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"companies"}, new Callable<List<CompanyEntity>>() {
      @Override
      @NonNull
      public List<CompanyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCompanyName = CursorUtil.getColumnIndexOrThrow(_cursor, "company_name");
          final int _cursorIndexOfImpactCount = CursorUtil.getColumnIndexOrThrow(_cursor, "impact_count");
          final int _cursorIndexOfLayoffStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "layoff_status");
          final int _cursorIndexOfIndustry = CursorUtil.getColumnIndexOrThrow(_cursor, "industry");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfReportedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "reported_at");
          final int _cursorIndexOfLogoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "logo_url");
          final int _cursorIndexOfTrendDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "trend_direction");
          final List<CompanyEntity> _result = new ArrayList<CompanyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CompanyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpCompanyName;
            _tmpCompanyName = _cursor.getString(_cursorIndexOfCompanyName);
            final Integer _tmpImpactCount;
            if (_cursor.isNull(_cursorIndexOfImpactCount)) {
              _tmpImpactCount = null;
            } else {
              _tmpImpactCount = _cursor.getInt(_cursorIndexOfImpactCount);
            }
            final String _tmpLayoffStatus;
            _tmpLayoffStatus = _cursor.getString(_cursorIndexOfLayoffStatus);
            final String _tmpIndustry;
            _tmpIndustry = _cursor.getString(_cursorIndexOfIndustry);
            final String _tmpLocation;
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            final long _tmpReportedAt;
            _tmpReportedAt = _cursor.getLong(_cursorIndexOfReportedAt);
            final String _tmpLogoUrl;
            if (_cursor.isNull(_cursorIndexOfLogoUrl)) {
              _tmpLogoUrl = null;
            } else {
              _tmpLogoUrl = _cursor.getString(_cursorIndexOfLogoUrl);
            }
            final String _tmpTrendDirection;
            _tmpTrendDirection = _cursor.getString(_cursorIndexOfTrendDirection);
            _item = new CompanyEntity(_tmpId,_tmpCompanyName,_tmpImpactCount,_tmpLayoffStatus,_tmpIndustry,_tmpLocation,_tmpReportedAt,_tmpLogoUrl,_tmpTrendDirection);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object clearAndInsert(final List<CompanyEntity> companies,
      final Continuation<? super Unit> $completion) {
    return CompanyDao.DefaultImpls.clearAndInsert(CompanyDao_Impl.this, companies, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
