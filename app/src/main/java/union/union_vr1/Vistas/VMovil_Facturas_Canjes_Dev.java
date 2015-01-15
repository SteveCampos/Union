package union.union_vr1.Vistas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import union.union_vr1.R;
import union.union_vr1.Sqlite.CursorAdapter_Facturas_Canjes_Dev;
import union.union_vr1.Sqlite.DbAdapter_Canjes_Devoluciones;

import static union.union_vr1.R.layout.*;

public class VMovil_Facturas_Canjes_Dev extends Activity {
    private int idProducto;
    private DbAdapter_Canjes_Devoluciones dbHelperCanjes_Dev;
    private ListView listaFacturas;
    private int stock;
    private String idEstablec;
    private int idAgente;
    private String nomProducto;
    private int idCategoriaEstablec;
    private String nomEstablecimiento;
    private int cantidadV;
    private int devueltoV;
    private Context ctx = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(princ_facturas_canjes_dev);


        //--------------------------------------------

        dbHelperCanjes_Dev = new DbAdapter_Canjes_Devoluciones(this);
        dbHelperCanjes_Dev.open();

        listaFacturas = (ListView) findViewById(R.id.facturasbyid);

        Bundle bd = getIntent().getExtras();
        idProducto = bd.getInt("idProducto");
        idEstablec = bd.getString("idEstablec");
        idAgente = bd.getInt("idAgente");
        nomProducto = bd.getString("nomProducto");
        Cursor cr = dbHelperCanjes_Dev.nom_establecimiento(idEstablec);
        cr.moveToFirst();
        nomEstablecimiento = cr.getString(1);
        idCategoriaEstablec = cr.getInt(2);
        imprimeStock(idAgente, idProducto);
        listarFacturas_Productos(idProducto, idAgente, idEstablec, nomProducto);
    }

    private void imprimeStock(int id, int producto) {
        TextView stockView = (TextView) findViewById(R.id.stock_can_dev);
        Cursor cr = dbHelperCanjes_Dev.obtenerStock(id, producto);
        if (cr.moveToFirst()) {
            stock = cr.getInt(cr.getColumnIndex("disponible"));

            if(stock<0){
                stock=0;
                stockView.setText("Stock Disponible: " + stock);
            }else{
                stockView.setText("Stock Disponible: " + stock);
            }

        } else {
            stockView.setText("No Cuenta con Stock de este Producto");
        }


    }
    private void addNewTick(){

    }

    private void listarFacturas_Productos(int producto, final int idAgente, final String idEstablec, final String nomProducto) {
        Cursor cr = dbHelperCanjes_Dev.listaFacturasByProducto(producto, idAgente, idEstablec);

        if (cr.moveToFirst()) {
            if ( cr.getString(10)==null || cr.getString(10).equals("")  ) {

                String[] noEncontrado = {"Producto No encontrado:", "Detalle: " + nomProducto + "", "¿Ingresar Comprobante de Venta?", "Yes", "No"};
                ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, noEncontrado);
                listaFacturas.setAdapter(adapter);
                listaFacturas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i == 4) {
                            Intent back = new Intent(getApplicationContext(), VMovil_Evento_Canjes_Dev.class);
                            back.putExtra("idEstabX", idEstablec);
                            back.putExtra("idAgente", idAgente);
                            startActivity(back);
                        }
                        if (i == 3) {
                            mostrar_alertdialog_spinners(nomProducto);
                        }

                    }
                });

            } else {
                TextView vieFooter = (TextView) getLayoutInflater().inflate(R.layout.agregar_otro, null);
                //vieFooter.setText("Agregar Otro");
                listaFacturas.addFooterView(vieFooter);
                vieFooter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(stock==0){
                            Toast.makeText(getApplicationContext(),"Tiene Stock 0",Toast.LENGTH_SHORT).show();
                            mostrar_alertdialog_spinners_dev(nomProducto);
                        }else{
                        mostrar_alertdialog_spinners(nomProducto);
                        }
                    }
                });
                CursorAdapter_Facturas_Canjes_Dev cFac_Can_Dev = new CursorAdapter_Facturas_Canjes_Dev(getApplicationContext(), cr);
                listaFacturas.setAdapter(cFac_Can_Dev);
                listaFacturas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                        if(stock==0){
                            mostrar_alertdialog_spinners_regitrados_dev(cursor);
                            Toast.makeText(getApplicationContext(),"Tiene Stock 0",Toast.LENGTH_SHORT).show();
                        }else{
                        mostrar_alertdialog_spinners_regitrados(cursor);
                    }}
                });
            }

        } else {

            String[] noEncontrado = {"Producto No encontrado:", "Detalle: " + nomProducto + "", "¿Ingresar Comprobante de Venta?", "Yes", "No"};
            ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, noEncontrado);
            listaFacturas.setAdapter(adapter);
            listaFacturas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i == 4) {
                        Intent back = new Intent(getApplicationContext(), VMovil_Evento_Canjes_Dev.class);
                        back.putExtra("idEstabX", idEstablec);
                        back.putExtra("idAgente", idAgente);
                        startActivity(back);
                    }
                    if (i == 3) {
                        mostrar_alertdialog_spinners(nomProducto);
                    }

                }
            });
        }


    }


    //cuando hay registros anteriores

    private void mostrar_alertdialog_spinners_regitrados_dev(Cursor cursor) {

        cantidadV = cursor.getInt(10);
        devueltoV = cursor.getInt(17)+cursor.getInt(25);
        final int dev = cursor.getInt(25);
        final int can = cursor.getInt(17);

        String cantidad = cursor.getString(10);
        final String idDetalle = cursor.getString(1);
        String comprobante = cursor.getString(7);
        //-----------------------------------------
        String[] datos = comprobante.split("/");
        //-------
        final String importe = datos[3];
        //-----------------------------------------
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView title = new TextView(this);
        title.setText("Canje/Devolucion: " + nomProducto + "");

        builder.setCustomTitle(title);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout_spinners = inflater.inflate(prompts_canjes, null);
        final EditText cantidadText = (EditText) layout_spinners.findViewById(R.id.cantidad_can_dev_registrado);
        final Spinner spinnerTipoOp = (Spinner) layout_spinners.findViewById(R.id.can_dev_tipo_op_registrado);
        final Spinner spinnerCategoria = (Spinner) layout_spinners.findViewById(R.id.can_dev_categoria_registrado);
        if (cantidad.equals("1")) {
            cantidadText.setText(cantidad);
            cantidadText.setEnabled(false);
        }


        cantidadText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (cantidadText.getText().toString().trim() != "") {
                    cantidadText.setError(null);
                    cantidadText.setTextColor(Color.parseColor("#FE9A2E"));
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                int r = cantidadV - devueltoV;


                if (charSequence.length() !=0) {
                    String nro = charSequence.toString();
                    int charSec = Integer.parseInt(nro);
                    if (charSec>r) {

                        cantidadText.setText("");
                        Toast.makeText(getApplicationContext(), "Cantidad tiene que ser Menor o Igual: " + r + "", Toast.LENGTH_SHORT).show();
                    }
                    if(charSec==0){
                        cantidadText.setText("");
                        Toast.makeText(getApplicationContext(), "La Cantidad no puede ser 0", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (cantidadText.getText().toString().trim().equals("")) {
                    cantidadText.setError("Es Requerido");
                } else {
                    cantidadText.setError(null);
                    cantidadText.setTextColor(Color.parseColor("#FE9A2E"));
                }
            }
        });

        builder.setView(layout_spinners);
        builder.setCancelable(false);
        builder.setPositiveButton("Guardar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String cantidad = cantidadText.getText().toString();
                        String tipo_op = spinnerTipoOp.getSelectedItem().toString();
                        String categoria_op = spinnerCategoria.getSelectedItem().toString();
                        if (cantidad.trim().equals("")) {
                            cantidadText.setError("Es Requerido");
                            Toast.makeText(getApplicationContext(), "Por favor Ingrese Todos los Campos", Toast.LENGTH_SHORT).show();

                        } else {

                            actualizar_can_dev(tipo_op, categoria_op, cantidad, importe, idDetalle,dev,can);
                        }
                    }
                })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        builder.show();


        ArrayList<String> tipo_oper = new ArrayList<String>();

        tipo_oper.add("Devolucion");

        ArrayAdapter<String> opArray = new ArrayAdapter<String>(this, spinner_layout, tipo_oper);
        opArray.setDropDownViewResource(simple_spinner_item); // The drop down view
        spinnerTipoOp.setAdapter(opArray);
        ArrayList<String> item_categorias = new ArrayList<String>();
        item_categorias.add("Bueno");
        item_categorias.add("Malogrado");
        item_categorias.add("Reclamo");
        item_categorias.add("Vencido-Malo");

        ArrayAdapter<String> cateArray = new ArrayAdapter<String>(this, spinner_layout, item_categorias);
        cateArray.setDropDownViewResource(simple_spinner_item); // The drop down view
        spinnerCategoria.setAdapter(cateArray);

    }
    private void mostrar_alertdialog_spinners_regitrados(Cursor cursor) {

        cantidadV = cursor.getInt(10);
        devueltoV = cursor.getInt(17)+cursor.getInt(25);
        final int dev = cursor.getInt(25);
        final int can = cursor.getInt(17);

        String cantidad = cursor.getString(10);
        final String idDetalle = cursor.getString(1);
        String comprobante = cursor.getString(7);
        //-----------------------------------------
        String[] datos = comprobante.split("/");
        //-------
        final String importe = datos[3];
        //-----------------------------------------
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView title = new TextView(this);
        title.setText("Canje/Devolucion: " + nomProducto + "");

        builder.setCustomTitle(title);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout_spinners = inflater.inflate(prompts_canjes, null);
        final EditText cantidadText = (EditText) layout_spinners.findViewById(R.id.cantidad_can_dev_registrado);
        final Spinner spinnerTipoOp = (Spinner) layout_spinners.findViewById(R.id.can_dev_tipo_op_registrado);
        final Spinner spinnerCategoria = (Spinner) layout_spinners.findViewById(R.id.can_dev_categoria_registrado);
        if (cantidad.equals("1")) {
            cantidadText.setText(cantidad);
            cantidadText.setEnabled(false);
        }


        cantidadText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (cantidadText.getText().toString().trim() != "") {
                    cantidadText.setError(null);
                    cantidadText.setTextColor(Color.parseColor("#FE9A2E"));
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                int r = cantidadV - devueltoV;


                if (charSequence.length() !=0) {
                    String nro = charSequence.toString();
                    int charSec = Integer.parseInt(nro);
                    if (charSec>r) {

                        cantidadText.setText("");
                        Toast.makeText(getApplicationContext(), "Cantidad tiene que ser Menor o Igual: " + r + "", Toast.LENGTH_SHORT).show();
                    }
                    if(charSec==0){
                        cantidadText.setText("");
                        Toast.makeText(getApplicationContext(), "La Cantidad no puede ser 0", Toast.LENGTH_SHORT).show();
                    }
                    if (charSec>stock){
                        cantidadText.setText("");
                        Toast.makeText(getApplicationContext(), "No puede Pasar el Stock", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (cantidadText.getText().toString().trim().equals("")) {
                    cantidadText.setError("Es Requerido");
                } else {
                    cantidadText.setError(null);
                    cantidadText.setTextColor(Color.parseColor("#FE9A2E"));
                }
            }
        });

        builder.setView(layout_spinners);
        builder.setCancelable(false);
        builder.setPositiveButton("Guardar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String cantidad = cantidadText.getText().toString();
                        String tipo_op = spinnerTipoOp.getSelectedItem().toString();
                        String categoria_op = spinnerCategoria.getSelectedItem().toString();
                        if (cantidad.trim().equals("")) {
                            cantidadText.setError("Es Requerido");
                            Toast.makeText(getApplicationContext(), "Por favor Ingrese Todos los Campos", Toast.LENGTH_SHORT).show();

                        } else {

                            actualizar_can_dev(tipo_op, categoria_op, cantidad, importe, idDetalle,dev,can);
                        }
                    }
                })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        builder.show();


        ArrayList<String> tipo_oper = new ArrayList<String>();
        tipo_oper.add("Canje");
        tipo_oper.add("Devolucion");

        ArrayAdapter<String> opArray = new ArrayAdapter<String>(this, spinner_layout, tipo_oper);
        opArray.setDropDownViewResource(simple_spinner_item); // The drop down view
        spinnerTipoOp.setAdapter(opArray);
        ArrayList<String> item_categorias = new ArrayList<String>();
        item_categorias.add("Bueno");
        item_categorias.add("Malogrado");
        item_categorias.add("Reclamo");
        item_categorias.add("Vencido-Malo");

        ArrayAdapter<String> cateArray = new ArrayAdapter<String>(this, spinner_layout, item_categorias);
        cateArray.setDropDownViewResource(simple_spinner_item); // The drop down view
        spinnerCategoria.setAdapter(cateArray);

    }

    //Actualizar Datos Histo_venta_Detalle
    private void actualizar_can_dev(String tipo_op, String categoria_op, String cantidad, String importe, String idDetalle,int devuelto, int canjeado) {
        if (categoria_op.equals("Bueno")) {
            categoria_op = "1";
        }
        if (categoria_op.equals("Malogrado")) {
            categoria_op = "2";
        }
        if (categoria_op.equals("Reclamo")) {
            categoria_op = "3";
        }
        if (categoria_op.equals("Vencido-Malo")) {
            categoria_op = "4";
        }
        if (tipo_op.equals("Canje")) {
            tipo_op = "1";
            boolean estado = dbHelperCanjes_Dev.update_Canj(tipo_op, categoria_op, cantidad, importe, idDetalle,canjeado,idProducto,ctx);
            if (estado) {
                confirmar();

            } else {
                Toast.makeText(ctx, "Por favor Intente Nuevamente", Toast.LENGTH_SHORT).show();

            }
        }
        if (tipo_op.equals("Devolucion")) {
            tipo_op = "2";
            boolean estado = dbHelperCanjes_Dev.update_dev(tipo_op, categoria_op, cantidad, importe, idDetalle,devuelto,idProducto,ctx);
            if (estado) {
                confirmar();

            } else {
                Toast.makeText(ctx, "Por favor Intente Nuevamente", Toast.LENGTH_SHORT).show();

            }
        }
    }

    //Cuando no se encuentran los registros
    private void mostrar_alertdialog_spinners_dev(String producto) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView title = new TextView(this);
        title.setText("Canje/Devolucion: " + producto + "");

        builder.setCustomTitle(title);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout_spinners = inflater.inflate(prompts, null);
        final EditText nroCompro = (EditText) layout_spinners.findViewById(R.id.comprob_clave_dev_can);
        final EditText nroLote = (EditText) layout_spinners.findViewById(R.id.lote_can_dev);
        final EditText cantidadText = (EditText) layout_spinners.findViewById(R.id.cantidad_can_dev);
        final Spinner spinnerTipoOp = (Spinner) layout_spinners.findViewById(R.id.can_dev_tipo_op);
        final Spinner spinnerCategoria = (Spinner) layout_spinners.findViewById(R.id.can_dev_categoria);

        nroCompro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (nroCompro.getText().toString().trim() != "") {
                    nroCompro.setError(null);
                    nroCompro.setTextColor(Color.parseColor("#FE9A2E"));
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (nroCompro.getText().toString().trim().equals("")) {
                    nroCompro.setError("Es Requerido");
                } else {
                    nroCompro.setError(null);
                    nroCompro.setTextColor(Color.parseColor("#FE9A2E"));
                }
            }
        });

        nroLote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (nroLote.getText().toString().trim() != "") {
                    nroLote.setError(null);
                    nroLote.setTextColor(Color.parseColor("#FE9A2E"));
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (nroLote.getText().toString().trim().equals("")) {
                    nroLote.setError("Es Requerido");
                } else {
                    nroLote.setError(null);
                    nroLote.setTextColor(Color.parseColor("#FE9A2E"));
                }
            }
        });



        builder.setView(layout_spinners);
        builder.setCancelable(false);
        builder.setPositiveButton("Guardar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String compro = nroCompro.getText().toString();
                        String lote = nroLote.getText().toString();
                        String cantidad = cantidadText.getText().toString();
                        String tipo_op = spinnerTipoOp.getSelectedItem().toString();
                        String categoria_op = spinnerCategoria.getSelectedItem().toString();
                        if (compro.trim().equals("") || lote.trim().equals("") || cantidad.trim().equals("")) {
                            if (compro.trim().equals("")) {
                                nroCompro.setError("Es Requerido");
                            }
                            if (lote.trim().equals("")) {
                                nroLote.setError("Es Requerido");
                            }
                            if (cantidad.trim().equals("")) {
                                cantidadText.setError("Es Requerido");
                            }
                            Toast.makeText(getApplicationContext(), "Por favor Ingrese Todos los Campos", Toast.LENGTH_SHORT).show();

                        } else {
                            registrarNuevo_Comprobante(compro, lote, cantidad, tipo_op, categoria_op);
                        }
                    }
                })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        builder.show();


        ArrayList<String> tipo_oper = new ArrayList<String>();
        tipo_oper.add("Devolucion");

        ArrayAdapter<String> opArray = new ArrayAdapter<String>(this, spinner_layout, tipo_oper);
        opArray.setDropDownViewResource(simple_spinner_item); // The drop down view
        spinnerTipoOp.setAdapter(opArray);
        //spinnerTipoOp.setBackgroundColor(0xffffffff);
        ArrayList<String> item_categorias = new ArrayList<String>();
        item_categorias.add("Bueno");
        item_categorias.add("Malogrado");
        item_categorias.add("Reclamo");
        item_categorias.add("Vencido-Malo");

        ArrayAdapter<String> cateArray = new ArrayAdapter<String>(this, spinner_layout, item_categorias);
        cateArray.setDropDownViewResource(simple_spinner_item); // The drop down view
        spinnerCategoria.setAdapter(cateArray);

    }

    private void mostrar_alertdialog_spinners(String producto) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView title = new TextView(this);
        title.setText("Canje/Devolucion: " + producto + "");

        builder.setCustomTitle(title);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout_spinners = inflater.inflate(prompts, null);
        final EditText nroCompro = (EditText) layout_spinners.findViewById(R.id.comprob_clave_dev_can);
        final EditText nroLote = (EditText) layout_spinners.findViewById(R.id.lote_can_dev);
        final EditText cantidadText = (EditText) layout_spinners.findViewById(R.id.cantidad_can_dev);
        final Spinner spinnerTipoOp = (Spinner) layout_spinners.findViewById(R.id.can_dev_tipo_op);
        final Spinner spinnerCategoria = (Spinner) layout_spinners.findViewById(R.id.can_dev_categoria);

        nroCompro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (nroCompro.getText().toString().trim() != "") {
                    nroCompro.setError(null);
                    nroCompro.setTextColor(Color.parseColor("#FE9A2E"));
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (nroCompro.getText().toString().trim().equals("")) {
                    nroCompro.setError("Es Requerido");
                } else {
                    nroCompro.setError(null);
                    nroCompro.setTextColor(Color.parseColor("#FE9A2E"));
                }
            }
        });

        nroLote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (nroLote.getText().toString().trim() != "") {
                    nroLote.setError(null);
                    nroLote.setTextColor(Color.parseColor("#FE9A2E"));
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (nroLote.getText().toString().trim().equals("")) {
                    nroLote.setError("Es Requerido");
                } else {
                    nroLote.setError(null);
                    nroLote.setTextColor(Color.parseColor("#FE9A2E"));
                }
            }
        });

        cantidadText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (cantidadText.getText().toString().trim() != "") {
                    cantidadText.setError(null);
                    cantidadText.setTextColor(Color.parseColor("#FE9A2E"));
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (cantidadText.getText().toString().trim().equals("")) {
                    cantidadText.setError("Es Requerido");
                } else {
                    cantidadText.setError(null);
                    cantidadText.setTextColor(Color.parseColor("#FE9A2E"));
                }
            }
        });

        builder.setView(layout_spinners);
        builder.setCancelable(false);
        builder.setPositiveButton("Guardar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String compro = nroCompro.getText().toString();
                        String lote = nroLote.getText().toString();
                        String cantidad = cantidadText.getText().toString();
                        String tipo_op = spinnerTipoOp.getSelectedItem().toString();
                        String categoria_op = spinnerCategoria.getSelectedItem().toString();
                        if (compro.trim().equals("") || lote.trim().equals("") || cantidad.trim().equals("")) {
                            if (compro.trim().equals("")) {
                                nroCompro.setError("Es Requerido");
                            }
                            if (lote.trim().equals("")) {
                                nroLote.setError("Es Requerido");
                            }
                            if (cantidad.trim().equals("")) {
                                cantidadText.setError("Es Requerido");
                            }
                            Toast.makeText(getApplicationContext(), "Por favor Ingrese Todos los Campos", Toast.LENGTH_SHORT).show();

                        } else {
                            registrarNuevo_Comprobante(compro, lote, cantidad, tipo_op, categoria_op);
                        }
                    }
                })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        builder.show();


        ArrayList<String> tipo_oper = new ArrayList<String>();
        tipo_oper.add("Canje");
        tipo_oper.add("Devolucion");

        ArrayAdapter<String> opArray = new ArrayAdapter<String>(this, spinner_layout, tipo_oper);
        opArray.setDropDownViewResource(simple_spinner_item); // The drop down view
        spinnerTipoOp.setAdapter(opArray);
        //spinnerTipoOp.setBackgroundColor(0xffffffff);
        ArrayList<String> item_categorias = new ArrayList<String>();
        item_categorias.add("Bueno");
        item_categorias.add("Malogrado");
        item_categorias.add("Reclamo");
        item_categorias.add("Vencido-Malo");

        ArrayAdapter<String> cateArray = new ArrayAdapter<String>(this, spinner_layout, item_categorias);
        cateArray.setDropDownViewResource(simple_spinner_item); // The drop down view
        spinnerCategoria.setAdapter(cateArray);

    }

    private void registrarNuevo_Comprobante(String compro, String lote, String cantidad, String tipo_op, String catego_op) {

        int cantidad2 = Integer.parseInt(cantidad);
        int idTipo_Op = 0;
        int idCat_tipo = 0;
        //Evaluando Categoria
        if (catego_op.equals("Bueno")) {
            idCat_tipo = 1;
        }
        if (catego_op.equals("Malogrado")) {
            idCat_tipo = 2;
        }
        if (catego_op.equals("Reclamo")) {
            idCat_tipo = 3;
        }
        if (catego_op.equals("Vencido-Malo")) {
            idCat_tipo = 4;
        }
        //Obteniendo el nombre del establecimiento, categoria.

        String precio = dbHelperCanjes_Dev.obtenerPrecio(idProducto, idCategoriaEstablec, ctx,idEstablec);
        double importe = Double.parseDouble(precio);

        //Evaluando tipo de Operacion:
        if (tipo_op.equals("Canje")) {
            idTipo_Op = 1;
            boolean estado = dbHelperCanjes_Dev.insertarCanjes(idEstablec, idProducto, idTipo_Op, compro, nomEstablecimiento, nomProducto, idCat_tipo, cantidad2, importe*cantidad2, lote, idAgente);
            if (estado) {
                confirmar();
            } else {
                Toast.makeText(this, "No Inserto", Toast.LENGTH_SHORT).show();
            }
        }
        if (tipo_op.equals("Devolucion")) {
            idTipo_Op = 2;
            boolean estado = dbHelperCanjes_Dev.insertar_Dev(idEstablec, idProducto, idTipo_Op, compro, nomEstablecimiento, nomProducto, idCat_tipo, cantidad2, importe*cantidad2, lote, idAgente);
            if (estado) {
                confirmar();
            } else {
                Toast.makeText(this, "No Inserto", Toast.LENGTH_SHORT).show();

            }
        }


    }

    public void confirmar() {
        //Instancia para poder volver

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        alertDialogBuilder.setTitle("Guardado Correctamente");
        AlertDialog.Builder builder = alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Regresar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent back = new Intent(getApplicationContext(), VMovil_Evento_Canjes_Dev.class);
                        back.putExtra("idEstabX", idEstablec);
                        back.putExtra("idAgente", idAgente);
                        startActivity(back);
                        finish();

                    }

                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


/*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/
}