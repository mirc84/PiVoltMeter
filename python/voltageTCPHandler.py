import socketserver

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

        if stringdata == "start":
            self.start()
            return
        if stringdata == "stop":
            self.stop()
            return
        if stringdata == "activate_output":
            self.server.writer.activate()
            return
        if stringdata == "get_out_file":
            self.server.sendall(self.server.writer.filename)
            return
        if stringdata == "deactivate_output":
            self.server.writer.deactivate()
            self.acknowledge()
            return
        if stringdata == "clear_output":
            self.server.writer.clear_file()
            self.acknowledge()
            return
        if stringdata == "ack" and self.server.switch.is_active:
            self.send_voltage()
            return
        if stringdata == "get_filenames":
            self.send_filenames()
            return

        args = stringdata.split()
        if len(args) == 2:
            self.handle_method_value(args[0], args[1])
            return

        self.not_acknowledge()

    def handle_method_value(self, method, value):
        if method == "set_path":
            self.server.writer.set_file_path(value)
            return
        if method == "set_rate":
            self.server.rate_container.value = float(value)
            return
        if method == "get_recordeddata":
            lines = self.server.writer.get_recorded_values(value)
            self.request.sendall('\t'.join(lines))
            return

    def acknowledge(self):
        self.request.sendall(b"ack")        

    def not_acknowledge(self):
        self.request.sendall(b"nack")        

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
