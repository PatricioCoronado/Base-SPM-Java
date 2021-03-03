package base_spm;
import javax.swing.JOptionPane;

public class D //Clase para debug 
{
	private static int contador=0;
	static public void msg (String mensaje)
	{
		System.out.println(mensaje);
	}

	static public void error(String mensaje)
	{
		JOptionPane.showMessageDialog
		(null, mensaje, "¡error!",JOptionPane.ERROR_MESSAGE);
	}

	static public void ok(String mensaje)
	{
		JOptionPane.showMessageDialog
		(null, mensaje,"todo ok",JOptionPane.INFORMATION_MESSAGE);

	}
	static public void contador()
	{
		System.out.println(++contador);

	}
	static public void reset()
	{
		contador=0;
	}
}//clsas D