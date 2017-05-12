package com.chavez.eduardo.udbtour;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WebService extends Service {
    //Identificador para nuestro servicio
    int id=5555;
    //Un tag para mensajes Log.d
    static final String DEBUG="SERVICEDEBUG";
    //API_URL SERÁ CAMBIADO
    //POR SU URL OBTENIDA
    //DE SU CONSULTA
    //http://192.168.43.3:5010/test/40ec4f09106a1b9a
    static final String APIURL="https://gitlab.com/snippets/1661859/raw";
    //Cola de consultas de Volley
    RequestQueue requestQueue;
    //Consulta de Volley
    JsonObjectRequest request;
    //Administrador Notificaciones del sistema
    NotificationManager notificationManager;
    //Notificaciones del sistema
    NotificationCompat.Builder builder;

    int oldTamanio = 0;

    public WebService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        //TODO: Hay que retirar los logs
        Log.d(DEBUG,"El servicio se ha iniciado...");

        notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        requestQueue= Volley.newRequestQueue(this);

        //Notificacion
        builder = new NotificationCompat.Builder(this)
                .setContentTitle("Preparando")
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_stat_map)
                .setContentText("Acomodando maletas...")
                .setOngoing(false)
        ;

        //Inicio del servicio
        Intent resultIntent = new Intent(this, MarkersList.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MarkersList.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //Interruptor que manda la notificacion
        builder.setContentIntent(resultPendingIntent);
        notificationManager.notify(id,builder.build());

        //Consulta al webservice
        request= new JsonObjectRequest(APIURL, null, new
                Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Llamamos funciones
                            parseAndNotify(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(DEBUG,"Se ha consultado los datos en línea");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(DEBUG,"Un error ha sucedido" + error.getMessage());
                //En caso de fallo se llamará de manera recursiva request
                callAddQueueRetry();
            }
        });

        //Hilo de consultas
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        //Pedimos que se actulicen los datos.
                        Log.d(DEBUG,"Se ha programado una consulta");
                        requestQueue.add(request);
                        //El tiempo está dado en milisengundos
                        //por ello multiplicamos por 1000
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //Funciona que reinicia el servicio
    private void callAddQueueRetry() {
        requestQueue.add(request);
    }

    //Parseo
    private void parseAndNotify(JSONObject response) throws JSONException {

        JSONArray data = response.getJSONArray("result");
        int tamanio = data.length();
        int[] id2 = new int[tamanio];
        String[] nombre = new String[tamanio];
        String[] descripcion = new String[tamanio];
        double[] latitud = new double[tamanio ];
        double[] longitud = new double[tamanio];
        String[] imagen = new String[tamanio];
        String[] thumbnail = new String[tamanio];
        String[] categoria = new String[tamanio];

        for ( int i = 0; i < tamanio; i ++ ){
            JSONObject datos= data.getJSONObject(i);
            id2[i] = datos.getInt("id");
            nombre[i] = datos.getString("nombre");
            descripcion[i] = datos.getString("descripcion");
            latitud[i] = datos.getDouble("latitud");
            longitud[i] = datos.getDouble("longitud");
            imagen[i] = datos.getString("imagen");
            thumbnail[i] = datos.getString("thumbnail");
            categoria[i] = datos.getString("categoria");

        }

        sendMessageToActivity(id2, nombre, descripcion, latitud, longitud,
                imagen,thumbnail, categoria);
        if(tamanio > oldTamanio){
            builder.setContentTitle("Nuevos")
                    .setContentText("Agregamos mas sitios!");

            notificationManager.notify(id,builder.build());
            Log.d("valor", String.valueOf(oldTamanio));
            oldTamanio = tamanio;
        }



    }

    private void sendMessageToActivity(int[] id,String[] nombre,String[] descripcion,
                                       double[] latitud, double[] longitud,String[] imagen,
                                       String[] thumbnail,String[] categoria) {
        Intent intent = new Intent("WebService");
        // You can also include some extra data.
        intent.putExtra("id", id);
        intent.putExtra("nombre", nombre);
        intent.putExtra("descripcion", descripcion);
        intent.putExtra("latitud", latitud);
        intent.putExtra("longitud", longitud);
        intent.putExtra("imagen", imagen);
        intent.putExtra("thumbnail", thumbnail);
        intent.putExtra("categoria", categoria);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}

