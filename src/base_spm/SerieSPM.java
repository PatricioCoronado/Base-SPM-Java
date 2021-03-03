package base_spm;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
// https://fazecast.github.io/jSerialComm/
import com.fazecast.jSerialComm.SerialPort;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;
import java.awt.event.ActionEvent;
//
public class SerieSPM extends JFrame {
	boolean abierto=false;
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField puertoAbierto;
	private JTextField cadenaEnviar;
	private static JTextField recibido;
	public static SerialPort COM=null;
	private JComboBox<String> listaPuertos;
	private JButton abrirPuerto;
	private JButton botonEnviar;
	private Thread thread;
	private JLabel lblPuertoAbierto;
	private JLabel lblPuertos;
	public JTextField recepcion=null;
	/********************************************************************
	 * Ejecuta la aplicación
	 ********************************************************************/
	/*
	
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					//SerieSPM frame = new SerieSPM(recibido);//Llamada al constructor
					SerieSPM frame = new SerieSPM(recibido);//Llamada al constructor
					frame.setVisible(true);
				} 
				catch (Exception e){e.printStackTrace();}
			}
		});
	}//main
	*/
	
	/**************************************************************************
	 * Constructor
	 **************************************************************************/
	public SerieSPM(JTextField recepcion) 
	{
		this.recepcion=recepcion;
		inicializa();
	}
	public SerieSPM() {
		setResizable(false);
		inicializa();
	}
	private void inicializa()
	{
		//Icon y titulo del form
		URL iconURL = getClass().getResource("/base_spm/Conector.png");
		ImageIcon icon = new ImageIcon(iconURL);
		setIconImage(icon.getImage());
		setTitle("BaseSPM puerto serie");
		//Configuración del form
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 139);
		//JPanel del form
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);//Layout relative
		//Botón para abrir y cerrar el puerto
		if (abierto()) abrirPuerto = new JButton("cerrar");
		else abrirPuerto = new JButton("abrir");
		abrirPuerto.setBounds(21, 48, 104, 23);
		contentPane.add(abrirPuerto);
		//Listener del botón para abrir y cerrar el puerto
		abrirPuerto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				botonAbrirCerrarPuertoPulsado();
			}
		});
		//Combo con la lista de puertos
		listaPuertos = new JComboBox<String>();
		listaPuertos.setEditable(false);
		listaPuertos.setBounds(156, 48, 89, 23);
		contentPane.add(listaPuertos);
		//Indicador del puerto abierto
		puertoAbierto = new JTextField();
		puertoAbierto.setEditable(false);
		puertoAbierto.setBounds(269, 48, 133, 23);
		//puertoAbierto.setColumns(10);
		contentPane.add(puertoAbierto);
		//Botón enviar
		botonEnviar = new JButton("enviar");
		botonEnviar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				enviar();
			}
		});
		botonEnviar.setBounds(21, 119, 104, 23);
		contentPane.add(botonEnviar);
		//Cadena a enviar por el puerto
		cadenaEnviar = new JTextField("*idn?");
		cadenaEnviar.setBounds(159, 120, 243, 20);
		contentPane.add(cadenaEnviar);
		cadenaEnviar.setColumns(10);
		//Cadena recibida
		recibido = new JTextField();
		recibido.setEditable(false);
		recibido.setBounds(21, 193, 381, 20);
		contentPane.add(recibido);
		recibido.setColumns(10);
		JLabel lblRespuesta = new JLabel("respuesta");
		lblRespuesta.setBounds(49, 168, 76, 14);
		contentPane.add(lblRespuesta);
		// Rellena el JComboBox con el nombre de los puertos ordenados previamente
		// Incrementa y decrementa los número de puertos para activar los enteros como tal. Si no hay errores ordenando
		SerialPort[] puertosEnSistema = SerialPort.getCommPorts();//Lista de nombre de puertos
		int[] numeroPuertos;//Para guardar el número de puertos como int para poder ordenarlos
		int nPuertos=puertosEnSistema.length;//Longitud de la lista
		numeroPuertos = new int[nPuertos];//Array de enteros (ordenables) con el número de los puertos
		for(int i = 0; i < nPuertos; i++)//Lee los números de la lista de puertos
			numeroPuertos[i]=Integer.parseInt(puertosEnSistema[i].getSystemPortName().substring(3))+1;
		Arrays.parallelSort(numeroPuertos);//Ordena el array de enteros de números de puertos
		for(int i=0;i<numeroPuertos.length;i++)  	
			listaPuertos.addItem("COM"+(numeroPuertos[i]-1));//Pone los nombres de los puertos en el combo
		botonEnviar.setEnabled(false);//Al principio no se puede enviar
		
		lblPuertoAbierto = new JLabel("puerto abierto");
		lblPuertoAbierto.setBounds(269, 24, 133, 14);
		contentPane.add(lblPuertoAbierto);
		
		lblPuertos = new JLabel("puertos");
		lblPuertos.setBounds(159, 24, 76, 14);
		contentPane.add(lblPuertos);
	}

	/*****************************************************************
	 *  Método que responde a la pulsación del botón "Enviar"
	 *****************************************************************/
	private void enviar() 
	{
		String comandoEnviar = cadenaEnviar.getText()+'\r';
		byte[] bitesEnviar=comandoEnviar.getBytes();
		int numeroDeBites=bitesEnviar.length;
		//Evita enviar a un puerto que no exista o cerrado
		if(COM!=null && COM.isOpen() && numeroDeBites>1)  
		{
			COM.writeBytes(bitesEnviar,numeroDeBites);
		}
	}
	/*****************************************************************
	 *  Método para recibir desde otra clase un comando para enviar
	 *  por el puerto 
	 *****************************************************************/
	protected void enviaComando(String comando) 
	{
		String comandoEnviar = comando +'\r';
		byte[] bitesEnviar=comandoEnviar.getBytes();
		int numeroDeBites=bitesEnviar.length;
       //Evita enviar a un puerto que no exista o esté cerrado
		if(COM!=null && COM.isOpen() && numeroDeBites>1) 
		{
			COM.writeBytes(bitesEnviar,numeroDeBites);
		}
	}
	/*****************************************************************
	 *  Método para comprobar si un puerto existe en el sistema
	 *  devuelve true si el puerto está en el sistema y  false
	 *  si no
	 *****************************************************************/
	protected boolean existe(String puerto)
	{
		SerialPort[] puertos = SerialPort.getCommPorts();//Array de nombre de puertos
		for(int i=0;i< puertos.length;i++)//Recorre los puertos del sistema comparando con el parámetro pasado
		{
			if(puertos[i++].getSystemPortName().equals(puerto))
				return true;//Si encuentra el puerto sale con true
		}
		return false;//Si no encuentra el puerto sale con false
	}
	/*****************************************************************
	 *  Método que envia el comando recibido en un string
	 *****************************************************************/
	protected void println(String comando)
	{
		if(!COM.isOpen())
		{
			D.error("El puerto no está abierto");
			return;
		}
		String comandoEnviar = comando+'\r';
		byte[] bitesEnviar=comandoEnviar.getBytes();
		int numeroDeBites=bitesEnviar.length;
		if(COM!=null && COM.isOpen() && numeroDeBites>1) //Evita enviar a un puerto que no exista o cerrado 
		{
			COM.writeBytes(bitesEnviar,numeroDeBites);
		}
	}
	/*****************************************************************
	 *  Método que responde a la pulsación del botón "abrir puerto"
	 *****************************************************************/
	private void botonAbrirCerrarPuertoPulsado() 
	{
		if(abierto) //Si el puerto está abierto es que hay que cerrarlo
		{
			cerrar();
		}
		else //Pero si el puerto está cerrado se habre 
		{
			// Selecciona el puerto de la lista.

			COM = SerialPort.getCommPort(listaPuertos.getSelectedItem().toString());
			
			//Si el puerto ya está abierto sale sin hacer nada
			if(COM.isOpen())
			{
				D.error("el puerto está siendo usado");
				return;
			}
			//Si el puerto no está abierto lo abre
			abrirPuerto();
		}//else
	}
	/*************************************************************************
	 * Método para abrir el puerto y arrancar el thread de lectura del puerto
	 *************************************************************************/
	protected void abrirPuerto() 
	{
		if(COM.openPort())//Si consigue abrir el puerto lo configura y lanza el hilo 
			{
				COM.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 100);//100ms timeout escritura
				//puertoSerie.setFlowControl(puertoSerie.FLOW_CONTROL_DISABLED);
				COM.setComPortParameters(115200, 8, 1, 0);
				//Resto de configuraciones 
				abrirPuerto.setText("cerrar");//Cambia el mensaje del botón
				abierto=true;
				listaPuertos.setEnabled(false);//Inhabilita el JComboBox de puertos
				botonEnviar.setEnabled(true);//Se puede enviar
				//Muestra el puerto abierto
				puertoAbierto.setText(COM.getSystemPortName());
			/*------------------------------------------------------------
			Crea y ejecuta un thered que escucha el puerto serie abierto
			 y escribe lo recibido en "recibido"
			-------------------------------------------------------------*/
				thread = new Thread()
				{
					@Override public void run() 
					{
						//System.out.println(Thread.currentThread().getName());
						Scanner scanner = new Scanner(COM.getInputStream());
						while(scanner.hasNextLine())//Si encuentra '\n' cerrando la cadena recibida... 
						{
							try {
								//String line = scanner.nextLine();
								//recibido.setText(line);
								//if(recepcion!=null) recepcion.setText(line);
								BaseSPM.respuestaArduino(scanner);	
								
								
							} catch(Exception e) {recibido.setText("recibida cadena vacia");}
						}//while
						scanner.close();
					}//run
				};//Thread
				thread.start();//Ejecuta el thread
			}
		//Si no consigue habrir el puerto sale con error
		else 
			D.error("el puerto no ha abierto");
	}// método abrirPuerto()
	/*************************************************************************
	 * Método para interrumpir el thread de lectura y cerrar el puerto
	 *************************************************************************/
	protected void cerrar()
	{
		//D.msg("cerrando el puerto: puerto abierto= "+COM.isOpen());
		
		if(COM.isOpen())
		{
			thread.interrupt();
			COM.closePort();
			listaPuertos.setEnabled(true);
			abrirPuerto.setText("abrir");
			abierto=false;
			botonEnviar.setEnabled(false);//Se puede enviar
		}
		else D.error("El puerto no estaba abierto");
	}
	/*************************************************************************
	 * Método devuelve booleano a true su el puerto está abierto y false si no 
	 *************************************************************************/
	protected boolean abierto() 
	{
		return abierto;
	}
	/*************************************************************************/
}
