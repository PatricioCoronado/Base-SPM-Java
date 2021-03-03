package base_spm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

@SuppressWarnings("serial")
/**************************************************************************
 * 	Clase para dibujar los puntos del acelerómetro y 
 * 	el fotodiodo en en un JPane
 **************************************************************************/
public class JPanelGrafico extends JPanel 
{
	//Tamaño del JPanel para graficar
	int X=290;
	int Y=232;
	// gravedad
	double gx=0.0;
	double gy=0.0;
	//Puntos de gravedad en la gráfica
	int x=0;
	int y=0;
	// Valores del fotodiodo
	double Fn=0;
	double Fl=0;
	double Sum=0;
	//Valores del fotodiodo en la gráfica
	int fn;
	int fl;
	int sum;
	//Para saber que se está graficando
	public enum Tipo{FOTODIODO,ACELEROMETRO;}
	public Tipo tipoGrafica;
	boolean noInicializado;
	/**********************************************************************
	 * Constructor
	 **********************************************************************/
	public JPanelGrafico(Tipo tipo) 
	{
		tipoGrafica=tipo;
		setSize(X, Y);
		setBackground(Color.WHITE);
		noInicializado=true;
	}
	/**********************************************************************
	 * Metodo en el que se dibuja la gráfica
	 **********************************************************************/
	@Override
	public void paint(Graphics gLapiz)
	{
		Graphics2D lapiz = (Graphics2D) gLapiz;//El gráfico será en el plano 2D
		super.paint(lapiz);
		
		//Dibujo de la cuadrícula
		lapiz.setColor(Color.lightGray);
		for (int n=1;n<10;n++)//Cuadrícula
		{
			lapiz.drawLine(n*X/10, 0, n*X/10, Y);
			lapiz.drawLine(0,n*Y/10, X,n*Y/10);
		}
		lapiz.setColor(Color.DARK_GRAY);
		//Ejes principales
		lapiz.drawLine(X/2, 0, X/2, Y);
		lapiz.drawLine(0,Y/2, X,Y/2);
		
		if(noInicializado) return;
		
		//Punto		
		if(tipoGrafica==Tipo.ACELEROMETRO)
		{
			lapiz.setColor(Color.RED);
			lapiz.fillOval(x, y, 10,10);
			lapiz.drawString("gx= "+ this.gx+"   gy="+this.gy, 30, 15);
		}
		if(tipoGrafica==Tipo.FOTODIODO)
		{
			lapiz.setColor(Color.RED);
			lapiz.drawLine(fn, 0, fn, Y);
			lapiz.drawLine(0, fl, X, fl);

			lapiz.setStroke(new BasicStroke(10));
			lapiz.drawLine(X-1,sum, X-1,Y-1);
	
			lapiz.drawString("Fn= "+ this.Fn+"   Fl= "+this.Fl+"   Sum= "+this.Sum , 30, 15);
		}
	}
	/**********************************************************************
	 * Método para actualizar un punto del acelerómetro y repintar
	 **********************************************************************/
	public void puntoAcelerometro(double gx, double gy)
	{
		noInicializado=false;
		this.gx=gx;
		this.gy=gy;
		x= (int) (X*gx+X/2.0)-5; 
		y= (int) (Y/2.0-Y*gy)-5;
		//Límites para no salir de la gráfica
		if (x>X)x=X-5;
		if (x<0)x=0+5;
		if (y>Y)y=Y-5;
		if (y<0)y=0+5;
		repaint();
	}
	/**********************************************************************
	 * Método para actualizar un punto del fotodiodo y repintar
	 **********************************************************************/
	public void puntoFotodiodo(double Fn, double Fl, double Sum)
	{
		noInicializado=false;
		sum=Math.abs(sum);
		this.Fn=Fn;
		this.Fl=Fl;
		this.Sum=Sum;
        fn= (int) ((X/2) *(1+Fl/12.0));
        fl= (int) ((Y/2) *(1-Fn/12.0));
        sum = (int) (Y *(1-Sum/12.0));
		repaint();
	}
}/*************** class JPanelGrafico extends JPanel  **********************/
