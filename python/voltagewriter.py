import threading
import os.path
import time
import csv

FOLDER = "/data/"

class VoltageWriter:

    def __init__(self):
        self.main_thread = threading.current_thread()
        self.working_thread = self.main_thread
        self.is_active = False

    def activate(self):
        self.is_active = True
        
    def deactivate(self):
        self.is_active = False

    def get_filenames(self):
        return os.listdir(FOLDER)

    def get_recorded_values(self, filename):
        file_path = FOLDER + filename
        if not os.path.isfile(file_path):
            return []
        with open(file_path) as csvfile:
            return csvfile.readlines()
    
    def write(self, v):
        self.wait_for_thread()

        if self.is_active == False:
            return
        if self.csv_writer is None:
            return
        
        now = time.strftime('%Y-%m-%d %H:%M:%S.%f')
        self.csv_writer.writerow([now, v])

    def set_file_path(self, path):
        self.wait_for_thread()
        self.filename = path
        self.close_file()
        if self.filename is None:
            self.current_csv_path = None
            self.csv_file = None
            self.csv_writer = None
            return
        
        self.current_csv_path = FOLDER + self.filename
        self.csv_file = open(self.current_csv_path, 'w')
        self.csv_writer = csv.writer(self.csv_file)

    def close_file(self):
        self.wait_for_thread()
        if self.csv_file is None:
            return

        self.csv_file.close()
        self.csv_writer = None

    def clear_file(self):
        self.wait_for_thread()
        if self.current_csv_path is None:
            return
        
        self.close_file()
        if os.path.isfile(self.current_csv_path):
            os.remove(self.current_csv_path)

        self.set_file_path(self.current_csv_path)

    def wait_for_thread(self):
        to_wait_thread = self.working_thread
        self.working_thread = threading.current_thread()
        if to_wait_thread == self.main_thread or to_wait_thread == self.working_thread:
            return

        to_wait_thread.join()
        

