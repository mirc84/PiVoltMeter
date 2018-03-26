import socketserver
import tcpmessages

class VoltageTCPHandler(socketserver.StreamRequestHandler):
    """
    The request handler class for our server.

    It is instantiated once per connection to the server, and must
    override the handle() method to implement communication to the
    client.
    """
    def handle(self):
        while True:
            # self.request is the TCP socket connected to the client
            data = self.rfile.readline()
            stringdata = data.decode()
            if stringdata == '':
                    return

            stringdata = stringdata.strip()
            self.handle_message(stringdata)
    
    def handle_message(self, stringdata):
        if stringdata == '':
            return

        print("received: {}".format(stringdata))

        if stringdata == tcpmessages.START:
            self.start()
            return
        if stringdata == tcpmessages.STOP:
            self.stop()
            return
        if stringdata == tcpmessages.ACTIVATE_OUTPUT:
            self.server.writer.activate()
            return
        if stringdata == tcpmessages.GET_OUT_FILENAME:
            self.server.sendall(self.server.writer.filename)
            return
        if stringdata == tcpmessages.DEACTIVATE_OUTPUT:
            self.server.writer.deactivate()
            return
        if stringdata == tcpmessages.CLEAR_OUTPUT:
            self.server.writer.clear_file()
            return
        if stringdata == tcpmessages.GET_VOLTAGE and self.server.switch.is_active:
            self.send_voltage()
            return
        if stringdata == tcpmessages.GET_STATUS:
            if self.server.switch.is_active:
                self.request.sendall(0)
            else:
                self.request.sendall(1)
            return
        if stringdata == tcpmessages.GET_EXISITING_FILES:
            self.send_filenames()
            return

        args = stringdata.split()
        if len(args) == 2:
            self.handle_method_value(args[0], args[1])
            return


    def handle_method_value(self, method, value):
        if method == tcpmessages.SET_PATH:
            self.server.writer.set_file_path(value)
            return
        if method == tcpmessages.SET_RATE:
            self.server.rate_container.value = float(value)
            return
        if method == tcpmessages.GET_RECORDEDDATA:
            lines = self.server.writer.get_recorded_values(value)
            self.request.sendall('\t'.join(lines))
            return

    def send_filenames(self):
        filenames = self.server.writer.get_filenames()
        self.request.sendall('\t'.join(filenames))

    def send_voltage(self):
        v = str(self.server.voltage_container.value) + '\n'
        answer = v.encode()
        print("sending: {}".format(v))
        self.request.sendall(answer)

    def start(self):
        self.server.switch.switch_on()
        self.send_voltage()

    def stop(self):
        self.server.switch.switch_off()
