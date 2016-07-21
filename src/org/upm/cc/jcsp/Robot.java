package org.upm.cc.jcsp;

import org.jcsp.lang.CSProcess;

/**
 * Modela el comportamiento de los robots que hacen los controles de calidad.
 * @author groman
 *
 */
public class Robot implements CSProcess{

	/**
	 * Indentificador del robot
	 */
	private int rid; 
	/**
	 * Control del recurso compartido
	 */
	ControlFabricaInterface fabrica;
	/**
	 * Registra los pedidos que han pasado por el robot 
	 */
	private boolean[] pedidosProcesados;
	/**
	 * Número de pedidos procesados por el robot
	 */
	private int nProcesos;

	public Robot(int rid, ControlFabricaInterface fabrica) {
		super();
		this.rid = rid;
		this.fabrica = fabrica;
		pedidosProcesados= new boolean [TipoFabrica.N_PEDIDOS()];
		for(int i=0; i < TipoFabrica.N_PEDIDOS(); i++) {
			pedidosProcesados[i] = false;
		}
		nProcesos = 0;
	}

	/**
	 * Modela el comportamiento del robot, es decir, procesa los pedidos que le van llegando
	 * hasta que ya no le quedan pedidos.
	 */
	@Override
	public void run() {
		while (nProcesos < TipoFabrica.N_PEDIDOS()) {

			// Simulamos que el robot tarda un poco en estar operativo
			TipoFabrica.dormir();
			
			System.out.println(this + ": Preparado para procesar pedidos");
			
			int pid= fabrica.procesarPedido(this.rid);
			
			nProcesos ++;
			System.out.println(this + ": Ha procesado el pedido " + pid + ". Lleva procesados " + nProcesos + " pedidos");
			if (pedidosProcesados[pid]) {
				throw new RuntimeException(this + ": El robot ha procesado dos veces el pedido " + pid);
			}
			pedidosProcesados[pid] = true;
		}
		System.out.println(this + ": Ha finalizado. Total pedidos procesados: " + nProcesos);
	}

	public int getRid() {
		return rid;
	}
	
	@Override
	public String toString() {
		return "[Robot(" + rid + ")]";
	}
	
	/**
	 * Comprueba que todos los pedidos han pasado por el robot
	 * @throws FabricaException En caso de que haya pedidos sin procesar
	 */
	public void checkRobot(){
		for (int i = 0; i < pedidosProcesados.length; i++){
			if (!pedidosProcesados[i]) {
				System.out.println(this + "ERROR!!!!! El robot no ha procesado el pedido " + i);
			}
		}
	}
}
