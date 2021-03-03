package base_spm;

/*************************************************
 * Variables globales a todas las aplicaciones
************************************************ */
class Global {
    //Tipos de respuesta que puede enviar Arduino
    public interface TipoRespuesta //Se utiliza Ej: Global.TipoRespuesta.PASOS
    {
        public static final int LO_ENVIADO = 0; //El comando enviado
        public static final int SIN_FIRMA = 1; //Sin caracters previos
        public static final int PASOS = 2; //SZ
        public static final int ACELEROMETRO = 3; // LC
        public static final int FOTODIODO = 4; // FT
        public static final int VARIABLES = 5; //BL
        public static final int CONTADOR = 6; //XT
        public static final int MARCHA_PARO = 7; //PM
        public static final int SENTIDO = 8; //WD
        public static final int FRECUENCIA = 9; //CR
        public static final int MOTOR_ACTIVO = 10; //MV
        public static final int RESOLUCION = 11; //RS
        public static final int ONDA = 12; //NN
        public static final int TEMPERATURA_HUMEDAD = 13; //Tespacio
        public static final int ESTADO = 14; //YY
        public static final int STOP = 15; //ZT motor parado
        public static final int VERSION = 16;// KK Versión software
        // ... poner los que necesites
    }
    //public static String miMAC = "98:D3:41:F5:AC:C0";//Base_SPM_20191136
    //public static String miMAC = "00:14:03:05:5D:DC";//BT_PCC4
    //public static String miMAC = "00:15:A6:00:51:4B";//BT18
    public static final byte  LF = 10;//Nueva línea  '\n'
    public static final byte  CR = 13;//Retorno de carro '\r'
    public static final int MAX_LON_STRING = 64;
    //Variables del estado de la base
    public static final int FRECUENCIA_INICIAL = 10;
    public static final int FRECUENCIA_MAXIMA = 100;
    public static final int FRECUENCIA_MINIMA = 0;
    public static final int R_256 = 256;
    public static final int R_512 = 512;
    public static final int R_1024 = 1024;
    public static final int R_2048 = 2048;
    public static final int RESOLUCION[]= {R_256,R_512,R_1024,R_2048};
    public static final int PASOS_MAXIMOS = 600000;
    public static final int PASOS_MINIMO = 0;
    public static final int PASOS_INICIAL = 0;
	public static final int RESOLUCION_INICIAL = 256;
	public static final int DATOS_FOTODIODO_INICIAL = 100;
	public static final int DATOS_ACELEROMETRO_INICIAL = 100;
	public static final int DATOS_MAX = 2000;
	public static final int DATOS_MIN = 1;
	
	public static final int X_INICIAL = 100;
	public static final int Y_INICIAL = 100;

    public static int TIEMPO_VIBRACION = 50;
    public static int VELOCIDAD_INICIAL=3;//Velocidad motores inicial

    public static boolean traficoVisible=true;
    //Motores
	public static final int NINGUNO = 0;
    public static final int Z1=1;
    public static final int Z2=2;
    public static final int Z3=3;
    public static final int Z1Z2=4;
    public static final int Z1Z3=5;
    public static final int Z2Z3=6;
    public static final int Z1Z2Z3=7;
    public static final int fotodiodoX=10;
    public static final int fotodiodoY=13;
    public static final int laserX=11;
    public static final int laserY=12;
	public static final Integer muestrasFotodiodo = 1;
    public static final Integer muestrasAcelerometro = 1;
	
}