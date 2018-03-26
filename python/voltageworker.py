import time
import threading
import socketserver
import csv
import os
import voltagewriter
import voltageserver

class Voltagedetector:

        def __init__(self, reader, writer, switch, value_container, rate_container):
                self.reader = reader
                self.writer = writer
                self.switch = switch
                self.value_container = value_container
                self.rate_container = rate_container

        def start(self):
                t = threading.Thread(target=self.start_reading, args=() )
                t.start()

        def start_reading(self):
                while True:
                        active = self.switch.is_active()
                        if active == False:
                                self.value_container.value = "-"                
                                continue

                        self.value_container.value = self.reader.read_voltage()
                        # print(" Voltage is: " + str("%.2f"%self.voltage)+"V")
                        self.writer.write(self.value_container.value)
                        time.sleep(self.rate_container.value)
