package org.upm.cc.jcsp;

import java.util.Queue;
import java.util.LinkedList;

import es.upm.babel.cclib.Monitor;
import es.upm.babel.cclib.Monitor.Cond;

public class ControlAlmacenMonitor implements ControlAlmacenInterface {

	/**
	 * Cola para almacenar los pedidos que van llegando
	 */
	private Queue<Integer> colaAlmacen; 

	/**
	 * Monitor para controlar el control de acceso
	 */
	private Monitor mutex = new Monitor();
	/**
	 * Condition para dejar esperando al almacén mientras llegan pedidos.
	 */
	private Cond almacen;

	public ControlAlmacenMonitor () {

		// colaAlmacen = new LinkedList();
		colaAlmacen = new LinkedList<Integer>();  
		almacen = mutex.newCond();
	}

	@Override
	public void almacenarPedido(int pid) {
		mutex.enter();
		// Añadimos el pedido a la cola de pedidos
		colaAlmacen.add(pid);

		almacen.signal();
		mutex.leave();
	}

	@Override
	public int colocarPedido() {
		mutex.enter();

		if (colaAlmacen.size() == 0){
			almacen.await();
		}

		// Sacamos el primer pedido de la cola y lo devolvemos
		int pedido = colaAlmacen.poll();

		mutex.leave();
		return pedido;
	}


}
