package base_spm;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.JScrollBar;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;
import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;
//import javax.swing.UIManager;
//import javax.swing.UnsupportedLookAndFeelException;
//import com.formdev.flatlaf.FlatDarkLaf;
//import com.formdev.flatlaf.FlatDarculaLaf;
//import com.formdev.flatlaf.FlatIntelliJLaf;
//import com.fazecast.jSerialComm.SerialPort;
import base_spm.JPanelGrafico.Tipo;
import java.util.Scanner;
import javax.swing.event.ChangeListener;
import com.fazecast.jSerialComm.SerialPort;
import javax.swing.event.ChangeEvent;
import javax.swing.SwingWorker;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import javax.swing.ButtonGroup;
//
public class BaseSPM  extends JFrame//Clase principal con función main
{
	/********************************************************************
	 * 					main()
	 ********************************************************************/
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					BaseSPM window = new BaseSPM();//Llamada al constructor
					window.setVisible(true);
				} 
				catch (Exception e){e.printStackTrace();}
			}
		});
	}
	private static final long serialVersionUID = 1L;
	/*********************************************************************
		Miembros de la clase principal
	**********************************************************************/
	private static SerieSPM serie;//Puerto serie
	private JComboBox<Integer> comboResolucion;
	//Itenms del menú
	private JMenuBar menuBar;
	private JMenu mnComandos;
	private JMenuItem imFotodiodo;
	private JMenuItem imAcelerometro;
	JPanelGrafico graficaAcelerometro;
	JPanelGrafico graficaFotodiodo;
	private JScrollBar scrollFrecuencia;
	private JSpinner spinnerPasos;
	private JRadioButton radioMotorZ1;
	private JRadioButton radioMotorZ2;
	private JRadioButton radioMotorZ3;
	private static JToggleButton bajarZ;
	private static JToggleButton subirZ;
	private static JToggleButton bajarCabeza;
	private static JToggleButton subirCabeza;
	private static JToggleButton izquierdaCabeza;
	private static JToggleButton derechaCabeza;
	private static JTextField acelerometroX;
	private static JTextField acelerometroY;
	private static JTextField fotodiodoFn;
	private static JTextField fotodiodoFl;
	private static JTextField fotodiodoSum;
	private JTextField humedad;
	private JTextField temperatura;
	private JButton datosFotodiodo;
	private JButton datosAcelerometro;	
	private JButton enviar;//Botón par enviar un comando por el puerto serie
	private JTextField comando;//Comando a enviar por el puerto serie
	private static JTextField respuesta;//Texto de la respuesta de la base
	private JLabel pasosContinuoDiscreto;
	private final int locX=Global.X_INICIAL;
	private final int locY=Global.Y_INICIAL;
	private static String[] valoresFotodiodo=null;
	private static String[] valoresAcelerometro=null;
	private static String[] valoresTemperaturaHumedad=null;
	private JRadioButton rdbtnFotodiodo;
	private JRadioButton rdbtnLaser;
	private final ButtonGroup grupoLaserFotodiodo = new ButtonGroup();
	private boolean runWorker=false;
	private boolean runHtworker=false;
	private static boolean nuevoDatoFotodiodo;
	private static boolean nuevoDatoAcelerometro;
	private static boolean nuevoDatoTemperaturaHumedad;
    private String ficheroJson ="C:\\Users\\"+  System.getProperty("user.name") +"\\AppData\\Local\\BaseJson.ini";
    private VariablesBaseSPM base;//Para guardar el estado de las variables de la base
	/*****************************************************************
	 * Constructor de la clase principal
	******************************************************************/
	public BaseSPM() 
	{
		base=new VariablesBaseSPM();//Para mantener actualizado el estado de las variables de la base
		setResizable(false);//No se puede cambiar el tamaño
		setBounds(locX, locY, 782, 600);//Tamaño y posición del form principal
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Al cerrar el form principal se cierra la aplicación
		setTitle("Base SPM");
		URL iconURL = getClass().getResource("/base_spm/Atoms.png");
		ImageIcon icon = new ImageIcon(iconURL);
		setIconImage(icon.getImage());
		addWindowListener(new LocalWindowListener());//Listener de eventos del form principal
		getContentPane().setLayout(null);//Layout tipo relativo. Los controles se colocan con coordenadas
		//Crea el menú en el form principal
		menu();	
		graficaAcelerometro = new JPanelGrafico(Tipo.ACELEROMETRO);
		graficaAcelerometro.setLocation(38, 45);
		getContentPane().add(graficaAcelerometro);
		//Pane para gráfica
		graficaFotodiodo = new JPanelGrafico(Tipo.FOTODIODO);
		graficaFotodiodo.setLocation(448, 45);
		getContentPane().add(graficaFotodiodo);
		//Selección de motores Z
		radioMotorZ1 = new JRadioButton("z1");
		radioMotorZ1.setSelected(true);
		radioMotorZ1.setBounds(68, 351, 42, 23);
		getContentPane().add(radioMotorZ1);
		radioMotorZ2 = new JRadioButton("z2");
		radioMotorZ2.setSelected(true);
		radioMotorZ2.setBounds(137, 351, 42, 23);
		getContentPane().add(radioMotorZ2);
		radioMotorZ3 = new JRadioButton("z3");
		radioMotorZ3.setSelected(true);
		radioMotorZ3.setBounds(201, 351, 42, 23);
		getContentPane().add(radioMotorZ3);
		//Botones para mover los motores de la base y cabeza
		subirZ = new JToggleButton(new SwingAction("z+","subir z"));
		subirZ.setBounds(266, 319, 55, 34);
		getContentPane().add(subirZ);
		bajarZ = new JToggleButton(new SwingAction("z-","bajar z"));
		bajarZ.setBounds(266, 364, 55, 34);
		getContentPane().add(bajarZ);
		//Control para seleccionar la frecuencia
		JLabel frecuenciaPps = new JLabel("frecuencia pps = "+ base.frecuencia);
		frecuenciaPps.setBounds(26, 419, 302, 16);
		getContentPane().add(frecuenciaPps);
		scrollFrecuencia = new JScrollBar();
		scrollFrecuencia.setValue(base.frecuencia);
		scrollFrecuencia.addAdjustmentListener(new AdjustmentListener() {
			//Listener que actualiza la variable de estado "frecuencia"	
			public void adjustmentValueChanged(AdjustmentEvent e) {
					base.frecuencia=scrollFrecuencia.getValue();
					String masInformacion="";
					if (base.frecuencia==0) masInformacion = " fecuencia contrlada por el DSP"; 
					frecuenciaPps.setText("frecuencia pps = "+ base.frecuencia + masInformacion);
				}
			});
		scrollFrecuencia.setOrientation(JScrollBar.HORIZONTAL);
		scrollFrecuencia.setBounds(26, 436, 707, 23);
		getContentPane().add(scrollFrecuencia);
		//Control para seleccionar los pasos a dar
		spinnerPasos = new JSpinner();
		spinnerPasos.setModel( new SpinnerNumberModel(0,0,600000,1));
		//SpinnerNumberModel(int value, int minimum, int maximum, int stepSize)
		spinnerPasos.addChangeListener(new ChangeListener() {
			//Listener que actualiza la variable de estado "pasos"	
			public void stateChanged(ChangeEvent e) 
				{
					base.pasos = (Integer)spinnerPasos.getValue();
					if(base.pasos>Global.PASOS_MAXIMOS)
					{
						base.pasos=Global.PASOS_MAXIMOS;
					}
					if (base.pasos==0) pasosContinuoDiscreto.setText("pasos continuos");
					else pasosContinuoDiscreto.setText("pasos discretos");
				}
			});

		spinnerPasos.setBounds(340, 196, 83, 26);
			//spinner.set
			getContentPane().add(spinnerPasos);
			
			//Combo para "resolución"
			comboResolucion = new JComboBox<Integer>();
			comboResolucion.addItem(256);
			comboResolucion.addItem(512);
			comboResolucion.addItem(1024);
			comboResolucion.addItem(2048);
			
			comboResolucion.setToolTipText("resoluci\u00F3n");
			comboResolucion.setBounds(338, 255, 83, 22);
			comboResolucion.addActionListener(new ActionListener() {
				//Listener para actualizar el valor de la resolución
				public void actionPerformed(ActionEvent e) 
				{
					base.resolucion=(int)comboResolucion.getSelectedItem();
				}
			});
			getContentPane().add(comboResolucion);
			//
			respuesta = new JTextField();
			respuesta.setEditable(false);
			respuesta.setBounds(448, 490, 281, 26);
			getContentPane().add(respuesta);
			respuesta.setColumns(10);
			//Instanciación del puerto serie al que se le pada el JText fiels para que escriva la respuesta
			serie = new SerieSPM(respuesta);
			//JTextField para escribir un comando
			comando = new JTextField();
			comando.setEnabled(false);
			comando.setColumns(10);
			comando.setBounds(161, 490, 281, 26);
			getContentPane().add(comando);
			//Botón para enviar un comando
			enviar = new JButton("enviar");
			enviar.setEnabled(false);
			enviar.addActionListener(new ActionListener() {
			//Acción del botón enviar
			public void actionPerformed(ActionEvent e) 
				{
					
					serie.println(comando.getText());
				}
			});
			enviar.setBounds(25, 490, 98, 26);
			getContentPane().add(enviar);
			
			acelerometroX = new JTextField();
			acelerometroX.setVisible(false);
			acelerometroX.setEditable(false);
			acelerometroX.setBounds(100, 12, 79, 20);
			getContentPane().add(acelerometroX);
			acelerometroX.setColumns(10);
			
			JLabel lblNewLabel = new JLabel("g(x)");
			lblNewLabel.setVisible(false);
			lblNewLabel.setBounds(54, 14, 28, 16);
			getContentPane().add(lblNewLabel);
			
			acelerometroY = new JTextField();
			acelerometroY.setVisible(false);
			acelerometroY.setEditable(false);
			acelerometroY.setColumns(10);
			acelerometroY.setBounds(233, 12, 79, 20);
			getContentPane().add(acelerometroY);
			
			JLabel lblY = new JLabel("g(y)");
			lblY.setVisible(false);
			lblY.setBounds(201, 14, 21, 16);
			getContentPane().add(lblY);
			
			fotodiodoFn = new JTextField();
			fotodiodoFn.setVisible(false);
			fotodiodoFn.setEditable(false);
			fotodiodoFn.setColumns(10);
			fotodiodoFn.setBounds(486, 12, 56, 20);
			getContentPane().add(fotodiodoFn);
			
			fotodiodoFl = new JTextField();
			fotodiodoFl.setVisible(false);
			fotodiodoFl.setEditable(false);
			fotodiodoFl.setColumns(10);
			fotodiodoFl.setBounds(584, 12, 56, 20);
			getContentPane().add(fotodiodoFl);
			
			fotodiodoSum = new JTextField();
			fotodiodoSum.setVisible(false);
			fotodiodoSum.setEditable(false);
			fotodiodoSum.setColumns(10);
			fotodiodoSum.setBounds(686, 12, 56, 20);
			getContentPane().add(fotodiodoSum);
			
			JLabel lblFn = new JLabel("Fn");
			lblFn.setVisible(false);
			lblFn.setBounds(457, 14, 21, 16);
			getContentPane().add(lblFn);
			
			JLabel lblFl = new JLabel("Fl");
			lblFl.setVisible(false);
			lblFl.setBounds(560, 14, 21, 16);
			getContentPane().add(lblFl);
			
			JLabel lblSum = new JLabel("Sum");
			lblSum.setVisible(false);
			lblSum.setBounds(647, 14, 36, 16);
			getContentPane().add(lblSum);
			
			humedad = new JTextField();
			humedad.setEditable(false);
			humedad.setColumns(10);
			humedad.setBounds(354, 127, 56, 20);
			getContentPane().add(humedad);
			
			temperatura = new JTextField();
			temperatura.setEditable(false);
			temperatura.setColumns(10);
			temperatura.setBounds(354, 62, 56, 20);
			getContentPane().add(temperatura);
			
			JLabel lblTemperatura = new JLabel("temperatura");
			lblTemperatura.setBounds(346, 33, 71, 16);
			getContentPane().add(lblTemperatura);
			
			JLabel lblHumedad = new JLabel("humedad");
			lblHumedad.setBounds(354, 99, 67, 16);
			getContentPane().add(lblHumedad);
			
			JLabel lblResolucin = new JLabel("resoluci\u00F3n");
			lblResolucin.setBounds(350, 231, 67, 16);
			getContentPane().add(lblResolucin);
			
			JLabel lblPuertoSerie = new JLabel("puerto serie");
			lblPuertoSerie.setBounds(26, 470, 97, 16);
			getContentPane().add(lblPuertoSerie);
			
			JLabel lblComando = new JLabel("comando");
			lblComando.setBounds(160, 470, 97, 16);
			getContentPane().add(lblComando);
			
			JLabel lblRespuesta = new JLabel("respuesta");
			lblRespuesta.setBounds(448, 470, 97, 16);
			getContentPane().add(lblRespuesta);
			//JButtons
			datosAcelerometro = new JButton("recibir muestras de aceler\u00F3metro");
			datosAcelerometro.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) 
				{
					comando.setText("MOT:IAC "+base.datosAcelerometro);
					serie.println(comando.getText());	
				}
			});
			datosAcelerometro.setBounds(38, 281, 290, 23);
			getContentPane().add(datosAcelerometro);
			
			datosFotodiodo = new JButton("recibir muestras del fotodiodo");
			datosFotodiodo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) 
				{
					comando.setText("MOT:IFO "+base.datosFotodiodo);
					serie.println(comando.getText());	
				}
			});
			datosFotodiodo.setBounds(448, 281, 290, 23);
			getContentPane().add(datosFotodiodo);
			
			//JToggleButtons
			bajarCabeza = new JToggleButton(new SwingAction("y-","cabeza -> y-"));
			bajarCabeza.setBounds(584, 391, 55, 34);
			getContentPane().add(bajarCabeza);
			
			izquierdaCabeza = new JToggleButton(new SwingAction("x-","cabeza -> x-"));
			izquierdaCabeza.setBounds(523, 359, 55, 34);
			getContentPane().add(izquierdaCabeza);
			
			derechaCabeza = new JToggleButton(new SwingAction("x+","cabeza -> x+"));
			derechaCabeza.setBounds(647, 359, 55, 34);
			getContentPane().add(derechaCabeza);
			
			subirCabeza = new JToggleButton(new SwingAction("y+","cabeza -> y+"));
			subirCabeza.setBounds(585, 326, 55, 34);
			getContentPane().add(subirCabeza);
			//Panel puerto serie
			serie.setVisible(false);
			pasosContinuoDiscreto = new JLabel("pasos continuos");
			pasosContinuoDiscreto.setBounds(336, 170, 121, 16);
			getContentPane().add(pasosContinuoDiscreto);
			
			rdbtnLaser = new JRadioButton("l\u00E1ser");
			rdbtnLaser.setSelected(true);
			grupoLaserFotodiodo.add(rdbtnLaser);
			rdbtnLaser.setBounds(433, 351, 83, 23);
			getContentPane().add(rdbtnLaser);
			
			rdbtnFotodiodo = new JRadioButton("fotodiodo");
			grupoLaserFotodiodo.add(rdbtnFotodiodo);
			rdbtnFotodiodo.setBounds(433, 370, 83, 23);
			getContentPane().add(rdbtnFotodiodo);
			//Lee el fichero de configuración y actualiza las variables del sistema
			if (leeConfiguracion())//Si el puerto leido existe en el sistema lo abre
			{
				SerieSPM.COM = SerialPort.getCommPort(base.puerto);
				serie.abrirPuerto();
			}
			//Hilos secundarios
			runWorker=true;
			graficaWorker.execute();//Hilo para actualizar las gráficas
			runHtworker=true;
			htWorker.execute();//Hilo para actualizar la humedad y la temparatura
	}
	/*****************************************************************
	 * Crea el menú 
	******************************************************************/
	private void menu() 
	{
		menuBar = new JMenuBar();//Crea la barra de menú de ámbito global
		setJMenuBar(menuBar);//Añade la barra de menú a la ventana
		//Submenú "puerto"
		JMenu mnPuerto = new JMenu("puerto");
		menuBar.add(mnPuerto);
			JMenuItem imAbrirPuerto = new JMenuItem("abrir/cerrar");
		imAbrirPuerto.addActionListener(new ActionListener() {
			//Abre la ventana para habrir o cerrar el puerto serie
			public void actionPerformed(ActionEvent e) 
			{
				serie.setLocation(locX+30,locY+20);
				serie.setVisible(true);
			}
		});
		mnPuerto.add(imAbrirPuerto);
		//menú para comprobar si el puerto está abierto			
		JMenuItem imTestPuerto = new JMenuItem("test");
		imTestPuerto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				String[] mensaje= {"",""};
				
				if (SerieSPM.COM!=null)
				{
					mensaje[1]=SerieSPM.COM.getSystemPortName();
					if(serie.abierto())	mensaje[0]="puerto abierto";
					else mensaje[0]="puerto cerrado";
				}
				else
				{
					mensaje[1]="no hay puerto seleccionado";
					mensaje[0]="selecciona un puerto";
				}
				
				
				JOptionPane.showMessageDialog
				(null, mensaje[0] , mensaje[1] ,JOptionPane.INFORMATION_MESSAGE);
			}
		});
		mnPuerto.add(imTestPuerto);
		//Submenú comandos
		mnComandos = new JMenu("comandos");//menú de ámbito global
		menuBar.add(mnComandos);
		
		
		JMenuItem imContador = new JMenuItem("contador");
		imContador.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comando.setText("mot:co ?");
				serie.println(comando.getText());
			}
		});
		mnComandos.add(imContador);
		
		
		JMenuItem imVersion = new JMenuItem("versi\u00F3n");
		imVersion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comando.setText("mot:ver?");
				serie.println(comando.getText());
			}
		});
		mnComandos.add(imVersion);
		JMenu mnError = new JMenu("error");
		mnComandos.add(mnError);
		JMenuItem imLeerError = new JMenuItem("leer");
		imLeerError.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comando.setText("err?");
				serie.println(comando.getText());
			}
		});
		mnError.add(imLeerError);
		JMenuItem imBorrarError = new JMenuItem("borrar");
		imBorrarError.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comando.setText("cls");
				serie.println(comando.getText());
			}
		});
		mnError.add(imBorrarError);
		menuBar.add(mnComandos);
		//Submenú configuración
		JMenu mnConfiguracion = new JMenu("configuración");
		JMenu mnDatos = new JMenu("datos");
		mnConfiguracion.add(mnDatos);
		imFotodiodo = new JMenuItem("fotodiodo");//de ámbito global
		imFotodiodo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				Integer[] opciones = {1,10,25,50,100,200,500,1000,1500,2000};
				
				try {
					base.datosFotodiodo = (Integer) JOptionPane.showInputDialog
					(null, "actualmente recibirías "+base.datosFotodiodo+ " muestras"+"\n" +"¿cuantas muestras quieres recibir ahora?","muestras fotodiodo", 3,null, opciones, opciones[3]);
				} catch (Exception e1) {}//Si pulsa cancel sale con Excepcion
			}
		});
		imAcelerometro = new JMenuItem("acelerómetro");//de ámbito global
		imAcelerometro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				Integer[] opciones = {1,10,25,50,100,200,500,1000,1500,2000};
				try {
					base.datosAcelerometro = (Integer) JOptionPane.showInputDialog
					(null, "actualmente recibirías "+base.datosAcelerometro+ " muestras"+"\n" +"¿cuantas muestras quieres recibir ahora?","muestras acelerómetro", 3,null, opciones, opciones[3]);
				} catch (Exception e1) {}//Si pulsa cancel sale con Excepcion
			}
		});
		mnDatos.add(imFotodiodo);	
		mnDatos.add(imAcelerometro);
		menuBar.add(mnConfiguracion);//Añade el submenú a la barra de menú
	}
	/*****************************************************************
	 * 		Acción de los JToggleButtons que mueven motores
	 *****************************************************************/
	@SuppressWarnings("serial")
	private class SwingAction extends AbstractAction 
	{
		public SwingAction(String nombre,String descripcion) 
		{
			putValue(NAME, nombre);
			putValue(SHORT_DESCRIPTION, descripcion);
			//putValue(SMALL_ICON, icono);
		}
		/*************************************************************
		 * Listener que detecta la pulsación de un JToggleButon
		 *************************************************************/
		public void actionPerformed(ActionEvent e) 
		{
			JToggleButton botonPulsado = (JToggleButton) e.getSource();
			if(!botonPulsado.isSelected())//Si se ha despulsado un botón hay que parar el motor activo
			{
				comando.setText("MOT:MP 0");
				serie.println(comando.getText());
				return;
			}
			//Pero si se ha pulsado un JToggleButton desactivo todos...
			bajarZ.setSelected(false);
			subirZ.setSelected(false);
			bajarCabeza.setSelected(false);
			subirCabeza.setSelected(false);
			izquierdaCabeza.setSelected(false);
			derechaCabeza.setSelected(false);
			botonPulsado.setSelected(true);//Menos el pulsado que lo dejamos activado
			//Llama al método de mover base o mover cabeza según el botón pulsado 
			if(e.getSource().equals(bajarZ) || e.getSource().equals(subirZ))
				moverBase(botonPulsado);
			else 
				moverCabeza(botonPulsado);
		}
		/**************************************************************
		 * Se ha pulsado un botón para mover un motor Z
		 **************************************************************/
		private void moverBase(JToggleButton botonPulsado) 
		{
			int sentido;
			int motorActivo = 0;
			if(botonPulsado.equals(subirZ)) sentido=1;
			else sentido=0;
			//Lee el estado de los switeches
			boolean z1On =  radioMotorZ1.isSelected();
			boolean z2On =  radioMotorZ2.isSelected();
			boolean z3On =  radioMotorZ3.isSelected();
			if(z1On && z2On && z3On) motorActivo= Global.Z1Z2Z3;
			if(z1On && !z2On && !z3On) motorActivo= Global.Z1;
			if(!z1On && z2On && !z3On) motorActivo =Global.Z2;
			if(!z1On && !z2On && z3On) motorActivo =Global.Z3;
			if(z1On && z2On && !z3On) motorActivo =Global.Z1Z2;
			if(z1On && !z2On && z3On) motorActivo=  Global.Z1Z3;
			if(!z1On && z2On && z3On) motorActivo= Global.Z2Z3;
			if(!z1On && !z2On && !z3On) motorActivo = Global.NINGUNO;
			if (motorActivo==0)//Si no hay ningun Z seleccionado avisa y sale
			{
				D.error("no hay ningún motor seleccionado");
				botonPulsado.setSelected(false);
				return;
			}
			comando.setText("MOT:MMP "+motorActivo+" "+base.resolucion+" "+base.frecuencia+" "+ sentido +" "+base.pasos);
			serie.println(comando.getText());
		}
		/**************************************************************
		 * Se ha pulsado un botón para mover un motor en la cabeza
		 **************************************************************/
		private void moverCabeza(JToggleButton botonPulsado) 
		{
			int motorActivo=0;
			int sentido=0;
			boolean flagLaserFotodiodo=false;//dispositivo activado en cabelza. True es laser y false fotodiodo (por defecto)			
			//
			if(grupoLaserFotodiodo.getSelection().equals(rdbtnLaser.getModel()))
				flagLaserFotodiodo=true;//Pero si el radiobutton seleccionado es "laser" se cambia el flag
			//Selección de motor y sentido
		    if(botonPulsado.equals(subirCabeza)){
		        sentido=1;
		        if(flagLaserFotodiodo)motorActivo=Global.laserY;
		        else motorActivo=Global.fotodiodoY;
		    }
		    else if(botonPulsado.equals(bajarCabeza)){
		        sentido=0;
		        if(flagLaserFotodiodo)motorActivo=Global.laserY;
		        else motorActivo=Global.fotodiodoY;
		    }
		    else if(botonPulsado.equals(izquierdaCabeza)){
		        sentido=1;
		        if (flagLaserFotodiodo)motorActivo=Global.laserX;
		        else motorActivo=Global.fotodiodoX;
		    }
		    else if(botonPulsado.equals(derechaCabeza)){
		        sentido=0;
		        if (flagLaserFotodiodo)motorActivo=Global.laserX;
		        else motorActivo=Global.fotodiodoX;
		    }
		    comando.setText("MOT:MMP " + motorActivo +" "+ base.resolucion +" "+ base.frecuencia +" "+ sentido +" " + base.pasos +" \r");
		    serie.println(comando.getText());
		}	
	}
	/******************************************************************
	 *  Clase interna que implementa la interface WindowListener.
	 *  Responde a eventos del JFrame principal.
	 ******************************************************************/
	class LocalWindowListener implements WindowListener
	{
		@Override	public void windowOpened(WindowEvent e) {}
		@Override	public void windowClosed(WindowEvent e){}
		@Override	public void windowIconified(WindowEvent e) {}
		@Override	public void windowDeiconified(WindowEvent e){}
		@Override	public void windowDeactivated(WindowEvent e) {}
		/*************************************************************
		 * Utilizamos el event windowsClosing para salvar el nombre
		 * del puerto serie en un fichero y cerrarlo.
		 *************************************************************/
		//@SuppressWarnings("unchecked")
		@Override	public void windowClosing(WindowEvent e) 
		{
			
			if (serie.abierto()) //Si hay un puerto abierto lo escribe en el fichero y lo cierra	
			{
				guardaConfiguracion();
	        	serie.cerrar(); //Antes de salir cerramos el puerto
			}
		}
		/*************************************************************
		 * Utilizamos el event windowsActivated para habilitar o 
		 * inhabilitar los controles que actuan sobre el puerto
		 * serie en función de si esta abierto o cerrado.
		 *************************************************************/
		@Override	public void windowActivated(WindowEvent e) 
		{
			if(serie.abierto() )
			{
				enviar.setEnabled(true);
				comando.setEnabled(true);
				subirZ.setEnabled(true);
				bajarZ.setEnabled(true);
				izquierdaCabeza.setEnabled(true);
				derechaCabeza.setEnabled(true);
				subirCabeza.setEnabled(true);
				bajarCabeza.setEnabled(true);
				datosAcelerometro.setEnabled(true);
				datosFotodiodo.setEnabled(true);
				mnComandos.setEnabled(true);
			}
			else
			{
				mnComandos.setEnabled(false);
				enviar.setEnabled(false);
				comando.setEnabled(false);
				subirZ.setEnabled(false);
				bajarZ.setEnabled(false);
				izquierdaCabeza.setEnabled(false);
				derechaCabeza.setEnabled(false);
				subirCabeza.setEnabled(false);
				bajarCabeza.setEnabled(false);
				datosAcelerometro.setEnabled(false);
				datosFotodiodo.setEnabled(false);
			}
		}
	}
	/*****************************************************************
	 * Función que se ejecuta desde la clase SerieSPM  cuando se 
	 * recibe algo por el puerto. Recibe un scanner con la respuesta
	 *  de Arduino. Se ejecuta dentro del Thread secundario de
	 * recepción del puerto serie
	 *****************************************************************/
	protected static void respuestaArduino(Scanner resp)
	{
		String respuestaArduino = resp.nextLine();
		respuesta.setText(respuestaArduino);
		String firma=respuestaArduino.substring(0, 2);
        //Analiza la respuesta buscando la firma
        //.............. Humedad temperatura
		if (firma.equals("T ")) 
		{ 

			valoresTemperaturaHumedad=respuestaArduino.split(" ");
			nuevoDatoTemperaturaHumedad=true;
			return;
			
		}
		//.............. Fotodiodo
		if (firma.equals("FT"))
        {
        	valoresFotodiodo=respuestaArduino.split(" ");//Se reciven los datos entre espacios
        	if(valoresFotodiodo!=null && valoresFotodiodo.length==4)// 4 datos, firma y 3 valores
        	{
        		fotodiodoFn.setText(valoresFotodiodo[1]);
        		fotodiodoFl.setText(valoresFotodiodo[2]);
        		fotodiodoSum.setText(valoresFotodiodo[3]);
        		nuevoDatoFotodiodo=true;//Dato nuevo, recien recibido
        		return;
        	}
        }
		//.............. Acelerómetro        
		if(firma.equals("LC")) 
        {
        	valoresAcelerometro = respuestaArduino.split(" ");//Se reciven los datos entre espacios
        	if(valoresAcelerometro!=null && valoresAcelerometro.length==3)// 3 datos, firma y 2 valores
			{
				acelerometroX.setText(valoresAcelerometro[1]);
				acelerometroY.setText(valoresAcelerometro[2]);
				nuevoDatoAcelerometro=true;//Dato nuevo, recien recibido
				return;
			}
        }
        //............... Motor parado
		if(firma.equals("ZP"))
       	{ //Todos los togglebutton se desactivan
        	bajarZ.setSelected(false);
			subirZ.setSelected(false);
			bajarCabeza.setSelected(false);
			subirCabeza.setSelected(false);
			izquierdaCabeza.setSelected(false);
			derechaCabeza.setSelected(false);
			return;
       	}
        if(firma.equals("XT")) 
        { 
        	return;
        }
	}
	/*****************************************************************
	 * SwingWorker para actuzlizar las gráficas del acelerometro y
	 * del fotodiodo en un hilo diferente del principal
	 *****************************************************************/
	 @SuppressWarnings("rawtypes")
	SwingWorker graficaWorker = new SwingWorker()
	 {
		 @Override
		protected Object doInBackground() throws Exception {
			while (runWorker==true)
			{
				//Si los valores del acelerómetro son correctos
				if(nuevoDatoAcelerometro && valoresAcelerometro!=null && valoresAcelerometro.length==3)
				{
					//Dibuja el punto del acelerómetro
					double gx=Double.parseDouble(valoresAcelerometro[1]);
					double gy=Double.parseDouble(valoresAcelerometro[2]);
					graficaAcelerometro.puntoAcelerometro(gx,gy);
					nuevoDatoAcelerometro=false;//Como ha pintado el dato ya no es nuevo
				}
				//Dibuja el punto del fotodiodo
				if(nuevoDatoFotodiodo && valoresFotodiodo!=null && valoresFotodiodo.length==4)
				{
					graficaFotodiodo.puntoFotodiodo
						(Double.parseDouble(valoresFotodiodo[1]),
						Double.parseDouble(valoresFotodiodo[2]),
						Double.parseDouble(valoresFotodiodo[3]));
						nuevoDatoFotodiodo = false;//Como ha pintado el dato ya no es nuevo
				}
				if(nuevoDatoTemperaturaHumedad && valoresTemperaturaHumedad!=null && valoresTemperaturaHumedad.length==4)
				{	
					temperatura.setText(valoresTemperaturaHumedad[1]+"ºC");
					humedad.setText(valoresTemperaturaHumedad[3]+"%");
					nuevoDatoTemperaturaHumedad=false;
				}
				
				Thread.sleep(200);
			}
			return null;
		}	
	};
	/*****************************************************************
	 * Lee de un fichero la configuración actual.
	 *  @return
	 * Si lanza una excepción al leer el fichero sale con "false"
	 * sin cambiar nada. 
	 * Si lee el fichero; las variables que estén en rango las modifica;
	 * si no, dejas las que están defecto.
	 * Si el puerto leido existe en el sistema sale con "true".
	 * Pero si el puerto no existe sale con "false". 
	 *****************************************************************/
	private boolean leeConfiguracion()
	{
		Long frecuenciaL,pasosL,datosAcelerometroL,datosFotodiodoL,resolucionL;
		JSONParser parser = new JSONParser(); //Objeto sobre el que leer el Json
		try 
		{
			Object obj = parser.parse(new FileReader(ficheroJson));//Le pasamos el path del fichero al objeto Json
			JSONObject datosJson = (JSONObject) obj;
			base.puerto =  (String) datosJson.get("puerto");
			//Hay que leer los enteros como long porque Json no guarda el tipo
			frecuenciaL=  (Long) datosJson.get("frecuencia");
			pasosL= (Long) datosJson.get("pasos");
			datosAcelerometroL= (Long) datosJson.get("acelerometro");
			datosFotodiodoL= (Long) datosJson.get("fotodiodo");
			resolucionL= (Long) datosJson.get("resolucion");
		} 
		catch (Exception e) 
		{
			// TODO si no se leen los datos del fichero se aplica la configuración por defecto
			D.error("no se ha podido leer el"+'\n'+ "fichero de configuración");
			//e.printStackTrace();
			return false;
		}

		//Si llega aquí ha leido  datos. Los actualizamos.
		base.frecuencia =  Math.toIntExact(frecuenciaL);
		base.pasos =  Math.toIntExact(pasosL);
		base.datosAcelerometro =  Math.toIntExact(datosAcelerometroL);
		base.datosFotodiodo =  Math.toIntExact(datosFotodiodoL);
		base.resolucion =  Math.toIntExact(resolucionL);
		//Filtramos para no rebasar máximos no mínimos
		if (base.frecuencia<Global.FRECUENCIA_MINIMA) base.frecuencia=Global.FRECUENCIA_MINIMA;
		if (base.frecuencia>Global.FRECUENCIA_MAXIMA) base.frecuencia=Global.FRECUENCIA_MAXIMA;
		//Filtro de "pasos"
		if (base.pasos>Global.PASOS_MAXIMOS) base.frecuencia=Global.PASOS_MAXIMOS;
		if (base.pasos<Global.PASOS_MINIMO) base.frecuencia=Global.PASOS_MINIMO;
		//Confirma que la resolución leida es válida
		boolean resolucionNoValida=true;//Flag para saber si hay una resolución correcta
		for(int i = 0;i<Global.RESOLUCION.length;i++)
		{	
		if (base.resolucion == Global.RESOLUCION[i])//Si encuentra una resolución correcta desactiva el flag
			{	
				resolucionNoValida=false;
				break;
			}
		}
		if(resolucionNoValida) base.resolucion=Global.RESOLUCION[Global.RESOLUCION.length-1];
		//Filtramos datos acelerómetro y fotodiodo
		base.datosFotodiodo = (base.datosFotodiodo > Global.DATOS_MAX) ? Global.DATOS_MAX : base.datosFotodiodo;  
		base.datosFotodiodo = (base.datosFotodiodo < Global.DATOS_MIN) ? Global.DATOS_MIN : base.datosFotodiodo;
		base.datosAcelerometro = (base.datosAcelerometro > Global.DATOS_MAX) ? Global.DATOS_MAX : base.datosFotodiodo;  
		base.datosAcelerometro = (base.datosAcelerometro < Global.DATOS_MIN) ? Global.DATOS_MIN : base.datosFotodiodo;
		//Comprobamos que el puerto leido existe en el sistema
		if (serie.existe(base.puerto)) return true;
		else return false;
	}
	/*****************************************************************
	 * Guarda en un fichero la configuración actual
	 *****************************************************************/
	@SuppressWarnings("unchecked")
	private void guardaConfiguracion()
	{
		FileWriter file = null;
		JSONObject obj = new JSONObject();
	    obj.put("puerto", SerieSPM.COM.getSystemPortName());
	    obj.put("pasos", base.pasos);
	    obj.put("resolucion", base.resolucion);
	    obj.put("frecuencia", base.frecuencia);
	    obj.put("acelerometro", base.datosAcelerometro);
	    obj.put("fotodiodo", base.datosFotodiodo);
	    
        try {
            file = new FileWriter(ficheroJson);
            file.write(obj.toJSONString());
        } catch (IOException ex1) {
            //ex1.printStackTrace();
        	D.error("error de fichero");
 
        } finally {
 
            try {
                file.flush();
                file.close();
            }
            catch (IOException ex2)
            {
            	D.error("error de fichero");
            }
        }
	}
	/*****************************************************************
	 * SwingWorker para actualizar la humedad y la temperatura
	 *****************************************************************/
	 @SuppressWarnings("rawtypes")
	SwingWorker htWorker = new SwingWorker()
	 {
		 @Override
		protected Object doInBackground() throws Exception {
			while (runHtworker==true)
			{
				if (serie.abierto())
					serie.println("mot:th?");
				Thread.sleep(2000);
			}
			return null;
		}	
	};
	/*********************************************************************
	 * Clase para guardar todas las variables de la base y cabeza
	 ****************************************************************** */
	class VariablesBaseSPM
	{
		String puerto;
		int frecuencia;
		int pasos;
		int resolucion;
		int datosFotodiodo;
		int datosAcelerometro;
		public VariablesBaseSPM()
		{
			puerto=null;//puerto untilizado
			frecuencia=Global.FRECUENCIA_INICIAL;
			pasos=Global.PASOS_INICIAL;
			resolucion=Global.RESOLUCION_INICIAL;
			datosFotodiodo = Global.DATOS_FOTODIODO_INICIAL;
			datosAcelerometro =Global.DATOS_ACELEROMETRO_INICIAL;
		}	
	}
}//********************** fin class BaseSPM  **************************
