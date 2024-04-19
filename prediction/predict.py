import pandas as pd
import numpy as np
from sklearn.linear_model import LinearRegression
import matplotlib.pyplot as plt
import os

# Find the first CSV file in the my_database folder
database_folder = "orientation_database"
csv_files = [file for file in os.listdir(database_folder) if file.endswith(".csv")]

if len(csv_files) == 0:
    print("No CSV files found in the database folder.")
    exit()

csv_file_path = os.path.join(database_folder, csv_files[0])

df = pd.read_csv(csv_file_path, sep=';')

df['id'] = np.arange(1, len(df) + 1)
df.set_index('id', inplace=True)
window_size = 10  # prediction elements, how many should we require, adjust size here

def generate_data(data, window_size):
    X = []
    y = []
    for i in range(len(data) - window_size):
        X.append(data[i:i+window_size])
        y.append(data[i+window_size])
    return np.array(X), np.array(y)

def generate_predictions(model, last_window):
    next_pred = model.predict(last_window).flatten()
    return next_pred

def plot_predictions(df, predicted_values, label):
    plt.figure(figsize=(10, 6))
    plt.plot(df.index, df[label], label=f'Actual {label}')
    plt.plot(df.index[-1] + np.arange(1, len(predicted_values) + 1), predicted_values, label=f'Predicted {label}', color='orange')
    plt.plot([df.index[-1], df.index[-1] + 1], [df[label].iloc[-1], predicted_values[0]])
    plt.xlabel('ID')
    plt.ylabel(label.capitalize())
    plt.legend()
    plt.title(f'Actual vs Predicted {label.capitalize()}')
    plt.show()

predicted_roll_values = []
predicted_pitch_values = []
predicted_yaw_values = []

last_roll_window = df['roll'].iloc[-window_size:].values.reshape(1, -1)
last_pitch_window = df['pitch'].iloc[-window_size:].values.reshape(1, -1)
last_yaw_window = df['yaw'].iloc[-window_size:].values.reshape(1, -1)

X_roll, y_roll = generate_data(df['roll'].values, window_size)
model_roll = LinearRegression()
model_roll.fit(X_roll, y_roll)

X_pitch, y_pitch = generate_data(df['pitch'].values, window_size)
model_pitch = LinearRegression()
model_pitch.fit(X_pitch, y_pitch)

X_yaw, y_yaw = generate_data(df['yaw'].values, window_size)
model_yaw = LinearRegression()
model_yaw.fit(X_yaw, y_yaw)

for i in range(10):
    next_roll_pred = generate_predictions(model_roll, last_roll_window)
    next_pitch_pred = generate_predictions(model_pitch, last_pitch_window)
    next_yaw_pred = generate_predictions(model_yaw, last_yaw_window)
    
    predicted_roll_values.append(next_roll_pred[0])  
    predicted_pitch_values.append(next_pitch_pred[0])
    predicted_yaw_values.append(next_yaw_pred[0])  
    
    last_roll_window = np.append(last_roll_window[:, 1:], next_roll_pred.reshape(1, -1), axis=1)
    last_pitch_window = np.append(last_pitch_window[:, 1:], next_pitch_pred.reshape(1, -1), axis=1)
    last_yaw_window = np.append(last_yaw_window[:, 1:], next_yaw_pred.reshape(1, -1), axis=1)

plot_predictions(df, predicted_roll_values, 'roll')
plot_predictions(df, predicted_pitch_values, 'pitch')
plot_predictions(df, predicted_yaw_values, 'yaw')
