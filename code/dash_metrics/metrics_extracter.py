import sqlite3
import pandas as pd
import os
import glob

def extract_metrics(db_directory, extracted_directory, prefix=""):
    db_files = glob.glob(db_directory + '/*.db')
    
    for db_file in db_files:
        base_name = os.path.basename(db_file)
        csv_file = prefix + base_name.replace('.db', '.csv')

        if not os.path.exists(os.path.join(extracted_directory, csv_file)):
            conn = sqlite3.connect(db_file)
            
            cursor = conn.cursor()
            cursor.execute("SELECT name FROM sqlite_master WHERE type='table' AND name='StreamMetrics';")
            if cursor.fetchone():
                df = pd.read_sql_query("SELECT * FROM StreamMetrics", conn)
                df.to_csv(os.path.join(extracted_directory, csv_file), index=False)
            else:
                print(f"Table 'StreamMetrics' not found in {db_file}")
            
            conn.close()

local_db_directory = './databases'
server_db_directory = './ssh/databases'
extracted_directory = './extracted_metrics'

extract_metrics(local_db_directory, extracted_directory)

extract_metrics(server_db_directory, extracted_directory, prefix="ssh_")

print("Extraction complete.")