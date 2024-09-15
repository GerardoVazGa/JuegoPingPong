package mx.jjpg.examenej2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MyCanvasView extends View {
    private Paint dibujo = new Paint();
    private int velocidad = 5;
    private float posX, posY;
    private float dx = 5, dy = 5;
    private float radio;
    private float margen = 0;
    private int vidas = 4;
    private float barraAncho = 300;
    private float barraAlto = 30;
    private float barPosX;
    private float barVelocidad = 5;

    private int tiempo = 60;
    private float motionTouchEventX=0f;
    private float motionTouchEventY=0f;

    private Thread hilo;


    public MyCanvasView(Context context) {
        super(context);
        dibujo.setStyle(Paint.Style.FILL);
        posX = getWidth() / 2;
        posY = getHeight() / 2;
        radio = 40;
        barPosX = (getWidth() - barraAncho) / 2;

        hilo = new Thread() {
            public synchronized void run(){
                while (true){
                    try{
                        Thread.sleep(1000);
                        tiempo++;
                        postInvalidate();
                    } catch (InterruptedException e) {

                    }
                }
            }
        };

        hilo.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        posX = w/2;
        posY = h/2;
        barPosX = (w - barraAncho)/2;
    }

    @Override
    protected  void onDraw(Canvas canvas){
        int ancho = getWidth();
        int alto = getHeight();

        super.onDraw(canvas);

        dibujo.setColor(Color.BLACK);
        canvas.drawRect(0, 0, ancho, alto, dibujo);

        if (vidas <= 3){


            dibujo.setColor(Color.GREEN);
            canvas.drawCircle(posX, posY, radio, dibujo);

            dibujo.setColor(Color.BLUE);
            canvas.drawRect(barPosX, alto - barraAlto, barPosX + barraAncho, alto, dibujo);

            dibujo.setColor(Color.WHITE);
            dibujo.setTextSize(50); // Tamaño del texto
            canvas.drawText("Vidas: " + vidas, 20, 40, dibujo);

            int minutos = tiempo / 60;
            int segundos = tiempo % 60;

            String minutosFormateados = minutos < 10 ? "0" + minutos : String.valueOf(minutos);
            String segundosFormateados = segundos < 10 ? "0" + segundos : String.valueOf(segundos);

            String tiempoTexto = "Time: " + minutosFormateados + ":" + segundosFormateados;

            canvas.drawText(tiempoTexto, ancho - 320, 40, dibujo);



            if (posY + radio > alto) {
                // Si el círculo supera el límite inferior, restablece su posición inicial
                posX = ancho / 2;
                posY = alto / 2;
                velocidad++;
                vidas++;
                if(velocidad > 8){
                    velocidad = 5; // Restablece la velocidad
                }
            } else {
                posX += dx * velocidad;
                posY += dy * velocidad;
            }

            // Detecta colisiones con los límites de la vista
            if (posX - radio < 0 || posX + radio > ancho) {
                dx = -dx; // Invierte la dirección en x
            }
            if (posY - radio < 0) {
                dy = -dy; // Invierte la dirección en y
            }
            if( posY + radio > alto - barraAlto && posX + radio > barPosX && posX - radio < barPosX + barraAncho){
                dy = -dy;
            }




        }else{
            dibujo.setColor(Color.BLUE);
            canvas.drawRect(0, 0, ancho, alto, dibujo);


            dibujo.setColor(Color.RED);
            dibujo.setTextSize(130);
            Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC);
            dibujo.setTypeface(typeface);
            canvas.drawText("Game Over", (ancho / 2) - 300 , alto / 2, dibujo);
            dibujo.setColor(Color.WHITE);
            dibujo.setTextSize(70);
            canvas.drawText("<--START-->", (ancho / 2 )-200, alto / 2+150, dibujo);

            tiempo = 0;
        }
        invalidate();
    }

    public void moverBarraConAcelerometro(float x) {
        // Mover la barra horizontalmente
        barPosX += x * barVelocidad;

        if (barPosX < 0) {
            barPosX = 0;
        } else if (barPosX + barraAncho > getWidth()) {
            barPosX = getWidth() - barraAncho;
        }

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        motionTouchEventX = event.getX();
        motionTouchEventY = event.getY();
        if(motionTouchEventY > 500 &&
                motionTouchEventY < 1400 &&
                motionTouchEventX > 300 && motionTouchEventX < 850){
            /*Toast.makeText(getContext(), "hola", Toast.LENGTH_SHORT).show();*/
            vidas = 0;
            posX = getWidth()/2;
            posY = getHeight()/2;
            velocidad = 5;
        }
        return true;
        //return super.onTouchEvent(event);
    }
}
