package org.upm.cc.jcsp;

import org.jcsp.lang.CSProcess;

/**
 * Modela el comportamiento del almacén
 * @author groman
 *
 */
public class Almacen implements CSProcess {
	
	/*
	 * Control del recurso compartido
	 */
	private ControlAlmacenInterface controlAlmacen;
	/**
	 * Marca a cierto aquellos pedidos que se han almacenado
	 */
	private boolean[] pedidosAlmacenados;
	
	/**
	 * Número de pedidos almacenados en el almcén
	 */
	private int nAlmacenados;
	
	public Almacen(ControlAlmacenInterface controlAlmacen) {
		this.controlAlmacen = controlAlmacen;
		pedidosAlmacenados= new boolean [TipoFabrica.N_PEDIDOS()];
		for(int i=0; i < TipoFabrica.N_PEDIDOS(); i++) {
			pedidosAlmacenados[i] = false;
		}
		nAlmacenados = 0;
	}

	/**
	 * Modela el comporamiento del almacén. Espera a que haya pedidos en la cola para ir colocándolos. 
	 */
	@Override
	public void run() {
		while (nAlmacenados < TipoFabrica.N_PEDIDOS()) {
			
			System.out.println(this + ": preparado para almacenar");
			
			// Colocamos los pedidos según van llegando
			int pid = controlAlmacen.colocarPedido();

			if (pedidosAlmacenados[pid]) {
				throw new RuntimeException(this + ": Se intenta almacenar dos veces el pedido " + pid);
			}
			pedidosAlmacenados[pid] = true;
			
			nAlmacenados ++;

			System.out.println(this + ": Pedido " + pid + " colocado en el almacén. "
					+ "Pedidos almacenados: "  + nAlmacenados);
			// Paramos para simular que tarda un rato en colocar el pedido
			
			TipoFabrica.dormir();

		}
		System.out.println(this + ": FINALIZADO.");
	}
	
	@Override
	public String toString() {
		return "[Almacen]"; 
	}
	
	/**
	 * Comprueba los resultados del almacén
	 * @throws FabricaException En caso de que se haya perdido algún pedido
	 */
	public void checkAlmacen() throws FabricaException {
		
		for(int i = 0; i < pedidosAlmacenados.length; i++) {
			if (!pedidosAlmacenados[i]) {
				throw new FabricaException("No se ha almacenado el pedido " + i);
			} 
		}
		
		System.out.println("Todos los pedidos (" + pedidosAlmacenados.length + ") se encuentran en el almacen!");
	}
	
	
}
