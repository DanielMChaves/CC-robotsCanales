package org.upm.cc.jcsp;


/**
 * Establece los valores de las constantes de la fábrica
 * @author groman
 *
 */
public class TipoFabrica {

	public enum TIPO_FABRICA {
		TIPO_0, TIPO_1, TIPO_2, TIPO_3, TIPO_4, TIPO_5, TIPO_6, TIPO_7, TIPO_8, TIPO_9, TIPO_10 
	}

	public enum TIPO_VELOCIDAD {
		MUY_LENTO, LENTO, NORMAL, RAPIDO, MUY_RAPIDO
	}

	public static int[] ESPERAS = {3000,1500,800,500,0}; 

	private static int N_PEDIDOS = 250;
	private static int N_ROBOTS = 25;
	public static int N_PROCS[];

	private static int N_VERIFICACIONES [] ;

	private static TIPO_VELOCIDAD VELOCIDAD = TIPO_VELOCIDAD.NORMAL;

	/**
	 * Establece el tipo de fabrica, es decir, el número de pedidos, el número de 
	 * robots y verificadores, así como el número de procesos que puede hacer un robot 
	 * sin ser verificado.
	 * @param tipo Tipo de fábrica a generar
	 * @throws FabricaException En caso de que los valores no sean correctos
	 */
	public static void setTipoFabrica (TIPO_FABRICA tipo) throws FabricaException {
		switch (tipo) { 
		case TIPO_0: // Usa los valores de inicializacion de los atributos
			break;
		case TIPO_1:
			setTipo1();
			break;
		case TIPO_2: 
			setTipo2();
			break;
		case TIPO_3:
			setTipo3();
			break;
		case TIPO_4:
			setTipo4();
			break;
		case TIPO_5:
			setTipo5();
			break;
		case TIPO_6:
			setTipo6();
			break;
		case TIPO_7:
			setTipo7();
			break;
		case TIPO_8:
			setTipo8();
			break;
		case TIPO_9:
			setTipo9();
			break;
		case TIPO_10:
			setTipo10();
			break;
		}		

		rellenarNProcs();
		checkTipoFabrica();
	}

	public static void setVelocidadFabrica(TIPO_VELOCIDAD velocidad) {
		VELOCIDAD =  velocidad;
	} 

	private static void setTipo1 () {
		N_PEDIDOS = 2;
		N_ROBOTS = 10;
	}
	private static void setTipo2 () {
		N_PEDIDOS = 20;
		N_ROBOTS = 50;
	}
	private static void setTipo3 () {
		N_PEDIDOS = 1000;
		N_ROBOTS = 10;
	}
	private static void setTipo4 () {
		N_PEDIDOS = 100;
		N_ROBOTS = 100;
	}
	private static void setTipo5 () {
		N_PEDIDOS = 100;
		N_ROBOTS = 300;
	}
	private static void setTipo6 () {
		N_PEDIDOS = 2000;
		N_ROBOTS = 10;
	}
	private static void setTipo7 () {
		N_PEDIDOS = 50;
		N_ROBOTS = 1000;
	}
	private static void setTipo8 () {
		N_PEDIDOS = 300;
		N_ROBOTS = 300;
	}
	private static void setTipo9 () {
		N_PEDIDOS = 500;
		N_ROBOTS = 500;
	}
	private static void setTipo10 () {
		N_PEDIDOS = 1000;
		N_ROBOTS = 1000;
	}

	/**
	 * Comprueba que los datos de la fábrica son correctos
	 * @throws FabricaException En caso de que los datos no sean correctos
	 */
	public static void checkTipoFabrica () throws FabricaException {

		if (N_PEDIDOS <= 0 || N_ROBOTS <= 0) {
			throw new FabricaException("El número de pedidos, de robots y de procesados "
					+ "debe ser mayor que 0. (" + N_PEDIDOS + "," + N_PROCS + "," + N_ROBOTS +
					")");
		}
	}


	public static int N_PEDIDOS() {
		return N_PEDIDOS;
	}

	public static int N_ROBOTS() {
		return N_ROBOTS;
	}

	public static int N_PROCS(int rid) {
		return N_PROCS[rid];
	}

	public static int N_VERIFICACIONES(int rid) {
		return N_VERIFICACIONES[rid];
	}

	private static void rellenarNProcs() {
		N_PROCS = new int [N_ROBOTS];
		N_VERIFICACIONES = new int [N_ROBOTS];
		for (int i=0; i < N_ROBOTS; i++) {
			N_PROCS[i] = getValorAleatorio();
			N_VERIFICACIONES[i] = N_PEDIDOS / N_PROCS[i];
		}
	}

	private static int getValorAleatorio () {
		boolean encontrado = false;
		int valor = 0;
		while (!encontrado) {
			valor = (int)(Math.random() * 100);
			if (valor > 0 && N_PEDIDOS % valor == 0) {
				encontrado = true;
			}
		}
		return valor;
	}


	public static void imprimirInformacion () {
		StringBuffer buffer = new StringBuffer();
		buffer.append("N_PEDIDOS: " + N_PEDIDOS + "\n");
		buffer.append("N_ROBOTS: " + N_ROBOTS + "\n");
		buffer.append("NPROCS " + N_PROCS.length);
		System.out.println(buffer);
	}

	public static void dormir () {
		TipoFabrica.sleep((int)(Math.random() * ESPERAS[VELOCIDAD.ordinal()]));
	}

	public static void sleep (int ms) {
		long initialTimeMillis = System.currentTimeMillis();
		long remainTimeMillis = ms;

		while (remainTimeMillis > 0) {
			try {
				Thread.sleep(remainTimeMillis);
			}
			catch (InterruptedException e) {
			}
			remainTimeMillis =
					ms - (System.currentTimeMillis() - initialTimeMillis);
		}
	}

	/**
	 * Funcion solo para los tests
	 */
	public static void setValuestoN_PROCS (int value) {
		for (int i=0; i < N_PROCS.length; i++) {
			N_PROCS[i] = value;
		}
	}


}
