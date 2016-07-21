package org.upm.cc.jcsp;

public interface ControlAlmacenInterface {
	
	/**
	 * Pone en la cola de espera del almacén el pedido <source>pid</source>
	 * @param pid Identificador del pedido
	 */
	public void almacenarPedido(int pid);

	/**
	 * Coloca el primer pedido que es encuentre en la cola de pedidos del almacén
	 * @return El identificador <source>pid</source> del pedido que se ha colocado en el almacén
	 */
	public int colocarPedido ();
	
}
