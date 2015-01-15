package union.union_vr1.Vistas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import union.union_vr1.R;
import union.union_vr1.Sqlite.CursorAdapterFacturas;
import union.union_vr1.Sqlite.CursorAdapterFacturas_Canjes;
import union.union_vr1.Sqlite.CursorAdapterFacturas_Canjes_Dev;
import union.union_vr1.Sqlite.CursorAdapter_Facturas_Canjes_Dev;
import union.union_vr1.Sqlite.DbAdapter_Canjes_Devoluciones;
import union.union_vr1.Sqlite.DbAdapter_Comprob_Cobro;

public class mostrar_can_dev_facturas extends TabActivity {
    private String idEstablec;
    private int idAgente;
    private TabHost tabHost;
    private DbAdapter_Canjes_Devoluciones dbHelper_CanDev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infor_mostrar_facturas_can_dev);

        dbHelper_CanDev = new DbAdapter_Canjes_Devoluciones(getApplication());
        dbHelper_CanDev.open();

        Bundle bl = getIntent().getExtras();
        idEstablec = bl.getString("idEstablec");
        idAgente = bl.getInt("idAgente");


        Button btn_guardar = (Button) findViewById(R.id.btn_fac_guardar);
        Button btn_cancelar = (Button) findViewById(R.id.btn_fac_cancel);

        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        TabHost.TabSpec spec = tabHost.newTabSpec("Tab1");
        spec.setContent(R.id.tab_Canjes);
        spec.setIndicator("Canjes");
        displayListCanjes();
        tabHost.addTab(spec);

        TabHost.TabSpec spec2 = tabHost.newTabSpec("Tab2");
        spec2.setContent(R.id.tab_Devoluciones);
        spec2.setIndicator("Devoluciones");
        displayListDevoluciones();
        tabHost.addTab(spec2);

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idTab = tabHost.getCurrentTab();
                if (idTab == 0) {
                    dialog_save("Canjes", 1);


                }
                if (idTab == 1) {
                    dialog_save("Devoluciones", 2);
                }

            }
        });
        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idTab = tabHost.getCurrentTab();
                if (idTab == 0) {
                    dialog_cancel("Canjes", 1, "st_in_canjes");

                }
                if (idTab == 1) {
                    dialog_cancel("Devoluciones", 2, "st_in_devoluciones");
                }
            }
        });

    }


    private void displayListCanjes() {
        TextView view_canjes = new TextView(getApplicationContext());
        TextView footer = new TextView(getApplicationContext());
        String textoView = "";
        Cursor cabecera = dbHelper_CanDev.obtener_cabecera(idEstablec);

        if (cabecera.moveToFirst()) {
            String cliente = cabecera.getString(6);
            String documento = cabecera.getString(7);
            String texto = "Fecha de Emision: " + getDatePhone() + "," +
                    "\nCliente: " + cliente + "," +
                    "\nDocumento: " + documento + "";
            textoView = texto;

        } else {
        }

        Cursor cr = dbHelper_CanDev.obtener_facturas_canjes(1, idEstablec);
        ListView lista_facturas = (ListView) findViewById(R.id.listViewCanjes);
        Cursor precio = dbHelper_CanDev.obtener_igv(1, idEstablec);
        String textFooter = "";
        if (precio.moveToFirst()) {
            textFooter = "Sub: " + precio.getString(1) + "" +
                    "\nIgv: " + precio.getString(2) + "" +
                    "\nTot: " + precio.getString(0) + "";
        }
        footer.setText(textFooter);
        lista_facturas.addFooterView(footer);
        if (cr.moveToFirst()) {
            view_canjes.setText(textoView);
            lista_facturas.addHeaderView(view_canjes);
            CursorAdapterFacturas_Canjes adapter_facturas = new CursorAdapterFacturas_Canjes(getApplicationContext(), cr);
            lista_facturas.setAdapter(adapter_facturas);

        } else {

        }


    }

    private void displayListDevoluciones() {
        TextView view_dev = new TextView(getApplicationContext());
        TextView footer = new TextView(getApplicationContext());
        Cursor cabecera = dbHelper_CanDev.obtener_cabecera(idEstablec);
        String textoAdpater = "";
        if (cabecera.moveToFirst()) {
            //obtener datos del cliente
            String cliente = cabecera.getString(6);
            String documento = cabecera.getString(7);
            textoAdpater = "Fecha de Emision: " + getDatePhone() + "," +
                    "\nCliente: " + cliente + "," +
                    "\nDocumento: " + documento + "";


        } else {
        }

        Cursor cr = dbHelper_CanDev.obtener_facturas_dev(2, idEstablec);
        ListView lista_facturas = (ListView) findViewById(R.id.listViewDevoluciones);
        Cursor precio = dbHelper_CanDev.obtener_igv_dev(2, idEstablec);
        String textFooter = "";
        if (precio.moveToFirst()) {
            textFooter = "Sub: " + precio.getString(1) + "" +
                    "\nIgv: " + precio.getString(2) + "" +
                    "\nTot: " + precio.getString(0) + "";
        }
        footer.setText(textFooter);
        lista_facturas.addFooterView(footer);
        if (cr.moveToFirst()) {
            view_dev.setText(textoAdpater);
            lista_facturas.addHeaderView(view_dev);
            CursorAdapterFacturas_Canjes_Dev adapter_facturas = new CursorAdapterFacturas_Canjes_Dev(getApplicationContext(), cr);
            lista_facturas.setAdapter(adapter_facturas);


        } else {
        }

    }

    public void dialog_save(final String op, final int operacion) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Seguro de Guardar");
        AlertDialog.Builder builder = alertDialogBuilder
                .setMessage("Operacion: " + op + "")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (operacion == 2) {
                            boolean estado = dbHelper_CanDev.guardarCambios_dev(operacion, idEstablec);
                            if (estado) {
                                exito();
                            } else {
                                Toast.makeText(getApplicationContext(), "Ocurrio un Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (operacion == 1) {
                            boolean estado = dbHelper_CanDev.guardarCambios(operacion, idEstablec);
                            if (estado) {
                                exito();
                            } else {
                                Toast.makeText(getApplicationContext(), "Ocurrio un Error", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void dialog_cancel(final String op, final int tipo, final String columna) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Seguro de Borrar Los Cambios");
        AlertDialog.Builder builder = alertDialogBuilder
                .setMessage("Operacion: " + op + "")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (tipo == 1) {
                            boolean estado = dbHelper_CanDev.cancelarCambios(tipo, idEstablec, columna);
                            if (estado) {

                                exito();
                            } else {
                                Toast.makeText(getApplicationContext(), "Ocurrio un Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (tipo == 2) {
                            boolean estado = dbHelper_CanDev.cancelarCambios_dev(tipo, idEstablec, columna);
                            if (estado) {
                                exito();
                            } else {
                                Toast.makeText(getApplicationContext(), "Ocurrio un Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mostrar_can_dev_facturas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getDatePhone() {
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formatteDate = df.format(date);
        return formatteDate;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:

                dialog_regresar();

                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                dialog_regresar();

                return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPause() {
        super.onPause();
        dialog_regresar();
    }

    public void exito() {


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setTitle("Exito.!");

        AlertDialog.Builder builder = alertDialogBuilder
                .setMessage("Guardado Correctamente")
                .setCancelable(false)
                .setPositiveButton("Regresar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent in = new Intent(getApplicationContext(), VMovil_Evento_Canjes_Dev.class);
                        in.putExtra("idEstabX", idEstablec);
                        in.putExtra("idAgente", idAgente);
                        startActivity(in);
                    }

                });


        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();

    }

    public void dialog_regresar() {


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setTitle("¿Regresar?");

        AlertDialog.Builder builder = alertDialogBuilder
                .setMessage("Ten en Cuenta que tienes que Realizar alguna Operacion")
                .setCancelable(false)
                .setPositiveButton("Regresar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent in = new Intent(getApplicationContext(), VMovil_Evento_Canjes_Dev.class);
                        in.putExtra("idEstabX", idEstablec);
                        in.putExtra("idAgente", idAgente);
                        startActivity(in);
                    }

                })
                .setNegativeButton("Quedarme", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();

    }
}
