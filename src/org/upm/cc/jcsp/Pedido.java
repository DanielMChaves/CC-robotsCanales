package org.upm.cc.jcsp;

import org.jcsp.lang.CSProcess;


/**
 * Clase que modela el comportamiento de un pedido pasando de robot en robot. 
 * @author groman
 *
 */
public class Pedido implements CSProcess {


	private ControlFabricaInterface cFabrica;
	private ControlAlmacenInterface cAlmacen;
	private int pid;

	public Pedido(int pid, ControlFabricaInterface cFabrica, ControlAlmacenInterface cAlmacen) {
		super();
		this.pid = pid;
		this.cFabrica = cFabrica;
		this.cAlmacen = cAlmacen;
	}

	/*
	 * Simula el comportamiento de un pedido, es decir, pasa por todos los robots, 
	 * para finalmente ser almacenado. 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		
		for (int i=0; i < TipoFabrica.N_ROBOTS(); i ++) {
			// Ponemos los pedidos a esperar entre procesado y procesado 
			TipoFabrica.dormir();
				
			System.out.println(this + ": Notificando que el pedido se encuentra preparado en el robot " + i);
	
			// Notificamos que ya se encuentra preparada 
			cFabrica.notificarLlegadaPedido(pid, i);
	
			System.out.println(this + ": Esperando a ser procesado");
			
			// Espera a ser procesado
			cFabrica.pasarControl(pid);

			System.out.println(this + ": Pedido procesado correctamente");
		}
		
		cAlmacen.almacenarPedido(pid);
		
		System.out.println(this + ": Finalizado");
	}
	
	@Override
	public String toString() {
		return "[Pedido(" + pid + ")]";
	}
	
}
