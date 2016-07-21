package org.upm.cc.jcsp;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Parallel;
import org.upm.cc.jcsp.TipoFabrica.TIPO_FABRICA;
import org.upm.cc.jcsp.TipoFabrica.TIPO_VELOCIDAD;

public class Fabrica {

	public static void main(String[] args) throws InterruptedException, FabricaException {
//		// Establece el tipo de fábrica (mas o menos pedidos, robots... y la velocidad de los procesos)
		TipoFabrica.setTipoFabrica(TIPO_FABRICA.TIPO_1);
		TipoFabrica.setVelocidadFabrica(TIPO_VELOCIDAD.RAPIDO);
		ControlFabricaCSP cFabrica = new ControlFabricaCSP(TipoFabrica.N_PEDIDOS(),TipoFabrica.N_ROBOTS(),TipoFabrica.N_PROCS);
		TipoFabrica.imprimirInformacion();
//
		ControlAlmacenInterface cAlmacen = new ControlAlmacenMonitor();
		// Tenemos los siguientes hilos:
		// 1 -- Almacen
		// N_PEDIDOS -- para los pedidos
		// N_ROBOTS -- para los robots
		// N_ROBOTS -- para los mecanicos
		// 1 -- para el servidor
		CSProcess[] threads = new CSProcess[1 +
		                                    TipoFabrica.N_PEDIDOS() + 
		                                    TipoFabrica.N_ROBOTS() + 
		                                    TipoFabrica.N_ROBOTS() +
		                                    1];

		// Añadimos los procesos al sistema
		int offset = 0;
		threads[offset] = new Almacen(cAlmacen);
		offset++;
		
		for (int i = 0; i < TipoFabrica.N_PEDIDOS(); i++) {
			threads[offset] = new Pedido(i, cFabrica,cAlmacen);
			offset++;
		}
		for (int i = 0; i < TipoFabrica.N_ROBOTS(); i++) {
			threads[offset] = new Robot (i,cFabrica);
			offset++;
		}	
		for (int i = 0; i < TipoFabrica.N_ROBOTS(); i++) {
			threads[offset] = new Mecanico(i,cFabrica);
			offset++;
		}	
		
		threads[offset] = cFabrica;
		Parallel sistema = new Parallel(threads);
	    sistema.run();	
	   
	}
}
  