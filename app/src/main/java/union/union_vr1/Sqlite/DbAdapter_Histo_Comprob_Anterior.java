package union.union_vr1.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import union.union_vr1.Conexion.DbHelper;

public class DbAdapter_Histo_Comprob_Anterior {

    public static final String HC_id_hist_comprob = "_id";
    public static final String HC_id_establec = "hc_in_id_establec";
    public static final String HC_id_producto = "hc_in_id_producto";
    public static final String HC_nom_producto = "hc_te_nom_producto";
    public static final String HC_cantidad = "hc_in_cantidad";
    public static final String HC_prom_anterior = "hc_te_prom_anterior";
    public static final String HC_devuelto = "hc_te_devuelto";
    public static final String HC_valor_unidad = "hc_in_valor_unidad";
    public static final String HC_id_agente = "hc_in_id_agente";

    public static final String TAG = "Histo_Comprob_Anterior";
    private DbHelper mDbHelper;
    private SQLiteDatabase mDb;

    //private static final int DATABASE_VERSION = 1;
    //private static final String DATABASE_NAME = "ProdUnion.sqlite";
    private static final String SQLITE_TABLE_Histo_Comprob_Anterior = "m_histo_comprob_anterior";
    private final Context mCtx;

    public static final String CREATE_TABLE_HISTO_COMPROB_ANTERIOR =
            "create table "+SQLITE_TABLE_Histo_Comprob_Anterior+" ("
                    +HC_id_hist_comprob+" integer primary key autoincrement,"
                    +HC_id_establec+" integer,"
                    +HC_id_producto+" integer,"
                    +HC_nom_producto+" text,"
                    +HC_cantidad+" integer,"
                    +HC_prom_anterior+" text,"
                    +HC_devuelto+" text,"
                    +HC_valor_unidad+" integer,"
                    +HC_id_agente+" integer);";

    public static final String DELETE_TABLE_HISTO_COMPROB_ANTERIOR = "DROP TABLE IF EXISTS " + SQLITE_TABLE_Histo_Comprob_Anterior;

    public DbAdapter_Histo_Comprob_Anterior(Context ctx) {
        this.mCtx = ctx;
    }

    public DbAdapter_Histo_Comprob_Anterior open() throws SQLException {
        mDbHelper = new DbHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public long createHistoComprobAnterior(
            int id_establec, int id_producto, String nom_producto, int cantidad,
            String prom_anterior, String devuelto, int valor_unidad, int id_agente){

        ContentValues initialValues = new ContentValues();
        initialValues.put(HC_id_establec,id_establec);
        initialValues.put(HC_id_producto,id_producto);
        initialValues.put(HC_nom_producto,nom_producto);
        initialValues.put(HC_cantidad,cantidad);
        initialValues.put(HC_prom_anterior,prom_anterior);
        initialValues.put(HC_devuelto,devuelto);
        initialValues.put(HC_valor_unidad,valor_unidad);
        initialValues.put(HC_id_agente,id_agente);

        return mDb.insert(SQLITE_TABLE_Histo_Comprob_Anterior, null, initialValues);
    }

    public void updateHistoComprobAnterior1(String idorig, String iddest, String vals){
        ContentValues initialValues = new ContentValues();
        initialValues.put(HC_id_establec,iddest);

        mDb.update(SQLITE_TABLE_Histo_Comprob_Anterior, initialValues,
                HC_id_establec+"=?",new String[]{idorig});
    }

    public void updateHistoComprobAnterior2(String idorig, String iddest, String vals){
        ContentValues initialValues = new ContentValues();
        initialValues.put(HC_id_establec,iddest);

        mDb.update(SQLITE_TABLE_Histo_Comprob_Anterior, initialValues,
                HC_id_establec+"=? AND "+HC_cantidad+"=?",new String[]{idorig, vals});
    }

    public boolean deleteAllHistoComprobAnterior() {

        int doneDelete = 0;
        doneDelete = mDb.delete(SQLITE_TABLE_Histo_Comprob_Anterior, null , null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;

    }

    public boolean deleteAllHistoComprobAnteriorById(String id) {

        int doneDelete = 0;
        doneDelete = mDb.delete(SQLITE_TABLE_Histo_Comprob_Anterior, HC_id_establec+"=?", new String[]{id});
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;

    }

    public Cursor fetchHistoComprobAnteriorByName(String inputText) throws SQLException {
        Log.w(TAG, inputText);
        Cursor mCursor = null;
        if (inputText == null  ||  inputText.length () == 0)  {
            mCursor = mDb.query(SQLITE_TABLE_Histo_Comprob_Anterior, new String[] {HC_id_hist_comprob,
                            HC_id_establec, HC_id_producto, HC_nom_producto, HC_cantidad, HC_prom_anterior,
                            HC_devuelto,HC_valor_unidad,HC_id_agente},
                    null, null, null, null, null);

        }
        else {
            mCursor = mDb.query(true, SQLITE_TABLE_Histo_Comprob_Anterior, new String[] {HC_id_hist_comprob,
                            HC_id_establec, HC_id_producto, HC_nom_producto, HC_cantidad, HC_prom_anterior,
                            HC_devuelto,HC_valor_unidad,HC_id_agente},
                    HC_id_establec + " like '%" + inputText + "%'", null,
                    null, null, null, null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public Cursor fetchAllHistoComprobAnterior() {

        Cursor mCursor = mDb.query(SQLITE_TABLE_Histo_Comprob_Anterior, new String[] {HC_id_hist_comprob,
                        HC_id_establec, HC_id_producto, HC_nom_producto, HC_cantidad, HC_prom_anterior,
                        HC_devuelto,HC_valor_unidad,HC_id_agente},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchAllHistoComprobAnterior0() {

        Cursor mCursor = mDb.query(SQLITE_TABLE_Histo_Comprob_Anterior, new String[] {HC_id_hist_comprob,
                        HC_id_establec, HC_id_producto, HC_nom_producto, HC_cantidad, HC_prom_anterior,
                        HC_devuelto,HC_valor_unidad,HC_id_agente},
                HC_id_establec + " = 0", null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchAllHistoComprobAnterior0A() {

        Cursor mCursor = mDb.query(SQLITE_TABLE_Histo_Comprob_Anterior, new String[] {HC_id_hist_comprob,
                        HC_id_establec, HC_id_producto, HC_nom_producto, HC_cantidad, HC_prom_anterior,
                        HC_devuelto,HC_valor_unidad,HC_id_agente},
                HC_id_establec + " = 0", null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchAllHistoComprobAnteriorByIdEst(String id) {

        Cursor mCursor = mDb.query(SQLITE_TABLE_Histo_Comprob_Anterior, new String[] {HC_id_hist_comprob,
                        HC_id_establec, HC_id_producto, HC_nom_producto, HC_cantidad, HC_prom_anterior,
                        HC_devuelto,HC_valor_unidad,HC_id_agente},
                HC_id_establec + " = " + id, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public void insertSomeHistoComprobAnterior() {

        createHistoComprobAnterior( 1, 1, "PAN", 1, "0/0", "0", 1, 1);
        createHistoComprobAnterior( 1, 2, "AGUA", 2, "0/0", "0", 1, 1);
        createHistoComprobAnterior( 2, 1, "PAN", 1, "0/0", "0", 1, 1);
        createHistoComprobAnterior( 2, 2, "AGUA", 2, "0/0", "0", 1, 1);
        createHistoComprobAnterior( 3, 2, "AGUA", 3, "0/0", "0", 1, 1);

    }

}