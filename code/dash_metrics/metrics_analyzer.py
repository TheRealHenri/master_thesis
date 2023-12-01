import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

file_path = './extracted_metrics/ssh_streams_metrics_1701375093949.csv'  
metrics_df = pd.read_csv(file_path)

first_timestamp = metrics_df['Timestamp'].min()
metrics_df['AdjustedTimestamp'] = metrics_df['Timestamp'] - first_timestamp

sns.set(style="whitegrid")

def plot_metric(dataframe, metric, title):
    plt.figure(figsize=(10, 6))
    sns.lineplot(x='AdjustedTimestamp', y=metric, hue='StreamId', data=dataframe)
    plt.title(title)
    plt.xlabel('Runtime (ms)')
    plt.ylabel(metric)
    plt.legend(title='StreamId', bbox_to_anchor=(1.05, 1), loc='upper left')
    plt.xticks(rotation=45)
    plt.tight_layout()
    plt.show()

plot_metric(metrics_df, 'RecordSendRate', 'Record Send Rate Over Time')
plot_metric(metrics_df, 'ProcessLatencyAvg', 'Average Process Latency Over Time')
plot_metric(metrics_df, 'RecordsPerRequestAvg', 'Average Records Per Request Over Time')
plot_metric(metrics_df, 'ProcessRate', 'Process Rate Over Time')
plot_metric(metrics_df, 'RequestLatencyAvg', 'Average Request Latency Over Time')
plot_metric(metrics_df, 'RequestLatencyMax', 'Maximum Request Latency Over Time')