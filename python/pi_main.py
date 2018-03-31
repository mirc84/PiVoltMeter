import threading
import random

import on_off_switch
import voltageserver
import voltageworker
import container
import voltagewriter
import pivoltagereader

def main():
    switch = on_off_switch.Switch()
    switch.switch_on()
    voltage_container = container.ValueContainer()
    container.value = "-"
    rate_container = container.ValueContainer()
    rate_container.value = 0.2
    writer = voltagewriter.VoltageWriter()
    reader = pivoltagereader.PiVoltageReader()

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
