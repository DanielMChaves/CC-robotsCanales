package org.upm.cc.jcsp;

public interface ControlFabricaInterface {
	
	/**
	 * Notifica que el pedido <source>pid<\source> se encuentra preparado para ser procesado por
	 * el robot <rid>
	 * @param pid Identificador del pedido
	 * @param rid Identiticador del robot
	 */
	public void notificarLlegadaPedido(int pid, int rid);
	
	/**
	 * Pone en espera el pedido <source>pid</source> hasta ser procesado
	 * @param pid Identificador del pedido
	 */
	public void pasarControl (int pid);
	
	/**
	 * El robot <source>rid</source> procesa el primer pedido que se encuentra en su cola de pedidos
	 * @param rid Indentificador del pedido
	 * @return el <source>pid</source> del pedido que ha procesado
	 */
	public int procesarPedido (int rid);
	
	/**
	 * Verifica el robot <source>rid</source> cuando este hay superado el numero N_PROCS de pedidos
	 * sin ser revisado.
	 * @param rid Identificador del Robot
	 * @return El número de pedidos procesados cuando ha saltado la verificación
	 */
	public int verificarRobot (int rid);
	
	/**
	 * Notifica que el robot <source>rid</source> ya ha completado su verificación y puede 
	 * continuar procesando pedidos normalmente 
	 * @param rid Identificador del robot
	 */
	public void notificarFinMantenimiento (int rid);
	
}
