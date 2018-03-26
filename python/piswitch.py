import RPi.GPIO as GPIO
import threading
import time

class Switch:

    def __init__(self, switch):
        self.switch = switch
        GPIO.setwarnings(False)
        GPIO.setmode(GPIO.BCM)
        GPIO.setup(18, GPIO.IN, pull_up_down=GPIO.PUD_UP)
        threading._start_new_thread(self.checkSwitch, () )
    
    def checkSwitch(self):
        while True:
            input_state = GPIO.input(18)
            # do nothing if button not pressed
            if input_state == True:
                continue

            # wait until pressed button is released
            current_state = GPIO.input(18)
            while current_state == False:
                time.sleep(0.01)
                current_state = GPIO.input(18)
            
            self.switch.toggle()
