import threading
import random

import on_off_switch
import voltageserver
import voltageworker
import container
import voltagewriter

class DummyReader:
    def __init__(self):
        self.value = 15

    def read_voltage(self):
        self.value = self.value + random.random() - 0.5
        if self.value < 1:
            self.value = self.value + 5
        if self.value > 15:
            self.value = self.value - 5
        return self.value

def main():
    switch = on_off_switch.Switch()
    voltage_container = container.ValueContainer()
    container.value = "-"
    rate_container = container.ValueContainer()
    rate_container.value = 0.2
    writer = voltagewriter.VoltageWriter()
    reader = DummyReader()

    print("Starting TCP Server")
    server = voltageserver.VoltageTCPServer(('', 42000), writer, switch, voltage_container, rate_container)
    
    detector = voltageworker.Voltagedetector(reader, writer, switch, voltage_container, rate_container)
    detector.start()
    server.serve_forever()

if __name__ =='__main__':
         try:
                  main()
         except KeyboardInterrupt:
                  pass
