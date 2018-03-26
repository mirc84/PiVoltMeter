
class Switch:
    
    def __init__(self):
        self.state = False

    def is_active(self):
        return self.state

    def toggle(self):
        if self.state == False:
            self.switch_on()
        else:
            self.switch_off()

    def switch_on(self):
        self.state = True
        print("Turned on")
                    
    def switch_off(self):
        self.state = False
        print("Turned off")
