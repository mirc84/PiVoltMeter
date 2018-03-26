import socketserver
import voltageTCPHandler

class VoltageTCPServer(socketserver.TCPServer):

    def __init__(self, server_address, writer, switch, voltage_container, rate_container):
        super().__init__(server_address, voltageTCPHandler.VoltageTCPHandler)
        self.voltage_container = voltage_container
        self.rate_container = rate_container
        self.switch = switch
        self.writer = writer