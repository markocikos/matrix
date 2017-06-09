import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';
import templates from './Matrix.soy';

class Matrix extends Component {
	created() {
		let websocket = this.websocket;

		if (websocket && websocket.readyState === 1) {
			websocket.close();
		}

		this.websocket = new WebSocket('ws://localhost:8080/o/matrixEndpoint');

		this.msgInput = document.querySelector('#msg');
	};

	desposed() {
		let websocket = this.websocket;

		if (websocket) {
			websocket.close();
		}
	};

	sendMessage(message) {
		let msgInput = this.msgInput;
		let websocket = this.websocket;

		if (msgInput && websocket) {
			websocket.send(msgInput.value);
		}
	};
}

Soy.register(Matrix, templates);

export default Matrix;