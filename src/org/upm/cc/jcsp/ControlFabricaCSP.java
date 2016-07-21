package org.upm.cc.jcsp;

import java.util.LinkedList;

import org.jcsp.lang.*;

public class ControlFabricaCSP implements ControlFabricaInterface, CSProcess {

	// Constantes de la clase
	private final int nPedidos;
	private final int nRobots;
	private final int N_PROCS [];

	// Los canales son lo único que puede ser compartido entre
	// diferentes procesos:
	Any2OneChannel chNotifLlegPed; // notificarLlegadaPedido()
	Any2OneChannel chNotifFinMant; // notificarFinMantenimiento()

	// Replicamos canales 
	Any2OneChannel[] chPasCont;    // pasarControl(pid)
	Any2OneChannel[] chProcPed;    // procesarPedido(rid)
	Any2OneChannel[] chVerifRob;   // verificarRobot(rid)

	// Interfaz ControlFabricaCSP
	public ControlFabricaCSP (int nPedidos, int nRobots, int [] N_PROCS) {

		// Inicializamos constantes de la clase
		this.nPedidos = nPedidos;
		this.nRobots = nRobots;
		this.N_PROCS = new int[N_PROCS.length];
		
		for(int i = 0; i < N_PROCS.length; i++){
			this.N_PROCS[i] = N_PROCS[i];
		}

		// Inicializamos los canales necesarios
		chPasCont  = new Any2OneChannel[this.nPedidos];
		chProcPed  = new Any2OneChannel[this.nRobots];
		chVerifRob  = new Any2OneChannel[this.nRobots];

		chNotifLlegPed = Channel.any2one();
		chNotifFinMant = Channel.any2one();

		// Creamos canales que faltan
		for (int pid = 0; pid < this.nPedidos; pid++) {
			chPasCont[pid] = Channel.any2one();
		}
		for (int rid = 0; rid < this.nRobots; rid++) {
			chProcPed[rid]  = Channel.any2one();
			chVerifRob[rid] = Channel.any2one();
		}
		
	}

	/**
	 * Clase auxiliar prara la peticion de notificarLlegada 
	 */

	class ParPR {

		public ParPR(int pid, int rid) {
			this.pid = pid;
			this.rid = rid;
		}
		public int pid;
		public int rid;

	}

	@Override
	public void notificarLlegadaPedido(int pid, int rid) {

		// Enviamos los datos de la notificación al servidor
		// COMPLETAR
		// OK
		One2OneChannel ch = Channel.one2one();
		ParPR pr = new ParPR(pid, rid);
		chNotifLlegPed.out().write(new Object[]{ch, pr});
	}

	@Override
	public void pasarControl(int pid) {

		// Replicación de canales: esperamos a que nos atiendan por
		// el canal correspondiente
		// COMPLETAR
		// OK
		chPasCont[pid].out().write(null);	
	}

	@Override
	public int procesarPedido(int rid) {

		// Replicación de canales: enviamos un nuevo canal a través del canal
		// correspondiente para recibir el identificador del pedido 
		// COMPLETAR
		// OK
		One2OneChannel ch = Channel.one2one();
		chProcPed[rid].out().write(new Object[]{ch, rid});

		// ... y esperamos respuesta con el identificador de pedido
		// COMPLETAR
		// OK
		//int pid = (Integer) ch.in().read();

		return (Integer) ch.in().read();  // y devolvemos el pedido procesado 
	}

	@Override
	public int verificarRobot(int rid) {

		// Enviamos un nuevo canal a través del canal
		// correspondiente para recibir el número de procesados 
		// COMPLETAR
		// OK
		One2OneChannel ch = Channel.one2one();
		this.chVerifRob[rid].out().write(new Object[]{ch, rid});

		// ... y esperamos respuesta con el numero de procesados
		// COMPLETAR
		// OK
		//int numProcesados = (Integer) ch.in().read();

		return (Integer) ch.in().read();   // y devolvemos el número de procesados
	}

	@Override
	public void notificarFinMantenimiento(int rid) {

		// Simplemente enviamos la notificación al servidor
		// por el canal correspondiente
		// COMPLETAR
		// OK
		One2OneChannel ch = Channel.one2one();
		chNotifFinMant.out().write(new Object[]{ch, rid});	
	}

	// Interfaz CSProcess
	@Override
	public void run() {

		// COMPLETAR: CREAMOS E INICIALIZAMOS LAS ESTRUCTURAS DE
		//            DATOS QUE CONTIENEN EL ESTADO DEL RECURSO
		// OK

		// Atributos para el recurso
		boolean pedidosEnEspera[] = new boolean[nPedidos];
		LinkedList<Integer>colaRobot[] = new LinkedList[nRobots];
		int numProcesados[] = new int[nRobots];
		boolean enMantenimiento[] = new boolean[nRobots];

		// Bucle nPedidos
		for(int i = 0; i < nPedidos; i++){
			pedidosEnEspera[i] = false;
		}

		// Bucle nRobots
		for(int i = 0; i < nRobots; i++){
			colaRobot[i] = new LinkedList<Integer>();
			numProcesados[i] = 0;
			enMantenimiento[i] = false;
		}

		// ESTRUCTURAS PARA RECEPCION ALTERNATIVA
		// habrá 1        canal   para notificarLlegadaPedido,
		//       nPedidos canales para pasarControl(pid),
		//       nRobots  canales para procesarPedido(rid),
		//       nRobots  canales para verificarRobot(rid), y
		//       1        canal   para notificarFinMantenimiento.
		// TOTAL: 2 + nPedidos + 2*nRobots entradas
		final Guard[] puertos = new AltingChannelInput[2+nPedidos+2*nRobots];
		// Asumiremos el siguiente orden:
		// 0                                        :-> notificarLlegadaPedido
		// 1..nPedidos                              :-> pasarControl(pid)
		// nPedidos+1 .. nPedidos+nRobots           :-> procesarPedido(rid)
		// nPedidos+nRobots+1 .. nPedidos+2*nRobots :-> verificarRobot(rid) 
		// nPedidos+2*nRobots+1                     :-> notificarFinMantenimiento(_)

		// Guardamos los indices de las operaciones en variables
		int idxNotificarLlegada = 0;
		int idxPasarControlIni = 1;
		int idxPasarControlFin = nPedidos;
		int idxProcesarPedidoIni = 1+nPedidos;
		int idxProcesarPedidoFin = nPedidos+nRobots;
		int idxVerificarRobotIni = nPedidos+nRobots+1;
		int idxVerificarRobotFin = nPedidos+2*nRobots;
		int idxNotificarFinMan = 1+nPedidos+2*nRobots;

		// Inicializamos las estructuras para la recepción alternativa
		puertos[idxNotificarLlegada] = chNotifLlegPed.in();
		for (int pid = 0; pid < this.nPedidos; pid++) {
			puertos[idxPasarControlIni+pid] = chPasCont[pid].in();
		}
		for (int rid = 0; rid < this.nRobots; rid++) {
			puertos[idxProcesarPedidoIni+rid] = chProcPed[rid].in();
			puertos[idxVerificarRobotIni+rid] = chVerifRob[rid].in();   
		}
		puertos[idxNotificarFinMan] = chNotifFinMant.in();

		final Alternative servicios = new Alternative(puertos);

		// Sincronización condicional en la select:
		final boolean[] sincCond = new boolean[2+nPedidos+2*nRobots];

		// COMPLETAR: ESTABLECER CONDICIONES DE RECEPCIÓN QUE NO
		//            CAMBIAN EN CADA ITERACIÓN DEL BUCLE DE SERVICIO


		// Variables auxiliares para comunicación con los clientes,
		// recepción alternativa, etc.  
		// COMPLETAR

		int petIndex;

		// Bucle de servicio
		while (true) {

			// Calculamos las CPREs de los métodos y las guardamos en el sincCond
			// COMPLETAR: CALCULAR LOS VALORES DE sincCond QUE CAMBIAN
			// OK
			sincCond[idxNotificarLlegada] = true;

			for(int i = idxPasarControlIni; i <= idxPasarControlFin; i++){
				sincCond[i] = !pedidosEnEspera[i - idxPasarControlIni];
			}

			for(int i = idxProcesarPedidoIni; i <= idxProcesarPedidoFin; i++){
				sincCond[i] = !colaRobot[i - idxProcesarPedidoIni].isEmpty() 
						&& !enMantenimiento[i - idxProcesarPedidoIni];
			}

			for(int i = idxVerificarRobotIni; i <= idxVerificarRobotFin; i++){
				sincCond[i] = enMantenimiento[i - idxVerificarRobotIni];
			}

			sincCond[idxNotificarFinMan] = true;

			// La select:
			petIndex = servicios.fairSelect(sincCond);

			// Tratamiento de peticiones por casos:
			if (petIndex == idxNotificarLlegada) { // notificarLlegadaPedido(p,r)

				// COMPLETAR: TRATAR PETICION DE NOTIFICAR LLEGADA
				// OK
				Object[] recibido = (Object []) chNotifLlegPed.in().read();

				ParPR par = (ParPR) recibido[1];
				int nuevoPid = par.pid;
				int nuevoRid = par.rid;

				pedidosEnEspera[nuevoPid] = true;
				colaRobot[nuevoRid].addLast(nuevoPid);

			} else if (idxPasarControlIni <= petIndex && petIndex <= idxPasarControlFin ) { // pasarControl(pid)

				// COMPLETAR: TRATAR PETICION DE PASAR CONTROL
				// OK
				chPasCont[petIndex - idxPasarControlIni].in().read();
				

			} else if (idxProcesarPedidoIni <= petIndex && petIndex <= idxProcesarPedidoFin ) { // procesarPedido

				// COMPLETAR: TRATAR PETICION DE PROCESAR PEDIDO
				// OK
				Object[] recibido = (Object[]) chProcPed[petIndex - idxProcesarPedidoIni].in().read();

				int rid = (Integer) recibido[1];

				int pid = colaRobot[rid].removeFirst();
				pedidosEnEspera[pid] = false;
				numProcesados[rid]++; 
				if(numProcesados[rid] % this.N_PROCS[rid] == 0){
					enMantenimiento[rid] = true;
				}

				((One2OneChannel) recibido[0]).out().write(pid);

			} else if (idxVerificarRobotIni <= petIndex && petIndex <= idxVerificarRobotFin) { //verificarRobot

				// COMPLETAR: TRATAR PETICION DE VERIFICAR ROBOT
				// OK
				Object[] recibido = (Object[]) chVerifRob[petIndex - idxVerificarRobotIni].in().read();

				((One2OneChannel) recibido[0]).out().write(numProcesados[(Integer) recibido[1]]);

			} else if (petIndex == idxNotificarFinMan) {  // notificarFinMantenimiento

				// COMPLETAR: TRATAR PETICION DE NOTIFICAR LLEGADA
				// OK
				Object[] recibido = (Object []) chNotifFinMant.in().read();

				int rid = (Integer) recibido[1];
				enMantenimiento[rid] = false;
			}
		}
	}
}
