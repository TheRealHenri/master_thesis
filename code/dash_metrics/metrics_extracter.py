import sqlite3
import pandas as pd

db_name = 'streams_metrics_'
db_name += '1701098222224'

conn = sqlite3.connect('./databases/' + db_name + '.db')

df = pd.read_sql_query("SELECT * FROM StreamMetrics", conn)

df.to_csv('./extracted_metrics/' + db_name  + '.csv', index=False)

conn.close()