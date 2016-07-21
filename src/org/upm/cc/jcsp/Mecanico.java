package org.upm.cc.jcsp;

import org.jcsp.lang.CSProcess;

/**
 * Modela el comportamiento de los mecánicos de los robots
 * @author groman
 *
 */
public class Mecanico implements CSProcess{

	private int rid; 
	private ControlFabricaInterface fabrica;
	private int nVerificaciones;
	
	public Mecanico(int rid, ControlFabricaInterface fabrica) {
		super();
		this.rid = rid;
		this.fabrica = fabrica;
		nVerificaciones = 0;
	}
	
	/**
	 * Modela el comportamiento del mecánico. Espera a que le indiquen que le toca verificar y 
	 * posteriormente notifica que ha terminado con el procesado.
	 */
	@Override
	public void run() {
		while (nVerificaciones < TipoFabrica.N_VERIFICACIONES(rid)) {

			System.out.println(this + ": Preparado para verificar el robot " + this.rid);
			
			int numProcs = fabrica.verificarRobot(this.rid);

			System.out.println(this + ": Empezando la verificacion del número de procesado: " + numProcs);
			
			if (numProcs % TipoFabrica.N_PROCS(rid) != 0) {
				throw new RuntimeException(this + ": Se ha procesado algún pedido cuando se debería haber verificado antes. El "
						+ "número de procesados (" + numProcs + ") no es múltiplo de N_PROCS " + TipoFabrica.N_PROCS(rid));
			}
			
			// Simulamos que el robot tarda un poco en ser verificado
			TipoFabrica.dormir();

			fabrica.notificarFinMantenimiento(rid);
			
			System.out.println(this + ": Notificado el fin de la verificacion del robot.");
			nVerificaciones ++;
		}
		System.out.println(this + ": Ha finalizado. Total verificaciones del mecánico: " + nVerificaciones);
	}

	public int getRid() {
		return rid;
	}
	
	@Override
	public String toString() {
		return "[Mecanico(" + rid + ")]";
	}

}
