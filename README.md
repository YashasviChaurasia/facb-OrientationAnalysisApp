# facb

![Untitled](facb%20f65cd6ec43c845f7ba6b10c95db884ef/Untitled.png)

# Orientation App with Accelerometer Sensors

The Orientation App with Accelerometer Sensors utilizes device sensors to provide real-time orientation data through graphical representation. This README provides an overview of the application structure, database schema, and details of each activity used.

## Application Structure

### Activities

1. **MainActivity**: The main entry point of the application. It initiates sensor data collection and navigates to the AccelerometerActivity.
2. **AccelerometerActivity**: Gathers sensor data from the device's accelerometer and displays real-time orientation data. It also allows exporting orientation data to a CSV file.
3. **GraphActivity**: Displays graphical representations of orientation data collected by the AccelerometerActivity using line charts. Users can visualize roll, pitch, and yaw angles over time and export data to a CSV file.

### Composable Functions

1. **FirstScreen**: A composable function representing the main screen of the application. It provides a button to initiate sensor data collection.
2. **AccelerometerScreen**: Displays real-time orientation data in the AccelerometerActivity. It also allows users to export orientation data to a CSV file.
3. **GraphScreen**: Displays line charts for roll, pitch, and yaw angles in the GraphActivity. Users can export orientation data to a CSV file.

# Accelerometer Activity

### **Sensor Management**

The activity initializes a **`SensorManager`** instance to interact with the device's sensors. It retrieves the default rotation vector sensor, which provides orientation data in the form of rotation vectors.

```kotlin
kotlinCopy code
private lateinit var sensorManager: SensorManager
private var rotationVectorSensor: Sensor? = null

```

### **Database Initialization**

An instance of the application's Room database (**`AppDatabase`**) is initialized within the **`onCreate()`** method of the activity. This allows for the storage of orientation data collected from the accelerometer.

```kotlin
kotlinCopy code
database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "my_database")
    .build()

```

### **Sensor Data Collection**

The activity implements the **`SensorEventListener`** interface to receive sensor events, specifically from the rotation vector sensor. The **`onResume()`** method registers the sensor listener with the sensor manager, specifying the desired sensor delay.

```kotlin
kotlinCopy code
override fun onResume() {
    super.onResume()
    rotationVectorSensor?.also { sensor ->
        sensorManager.registerListener(
            this,
            sensor,
            SensorManager.SENSOR_DELAY_FASTEST
        )
    }
}

```

The **`onSensorChanged()`** method is called whenever sensor data changes. Here, it retrieves the rotation matrix and orientation angles from the sensor event and converts the angles from radians to degrees. The orientation angles (roll, pitch, and yaw) are then used to update the UI.

```kotlin
kotlinCopy code
override fun onSensorChanged(event: SensorEvent) {
    if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
        // Convert angles from radians to degrees
        orientationAngles = orientationAngles.map { it * 180 / Math.PI.toFloat() }.toFloatArray()

        // Ensure that pitch value is not null
        val pitch = orientationAngles.getOrElse(1) { 0f }

        updateUI(orientationAngles[0], pitch, orientationAngles[2])
    }
}

```

### **UI Update**

The **`updateUI()`** function is responsible for updating the user interface with the latest orientation data. It recomposes the **`AccelerometerScreen`** composable function with the new roll, pitch, and yaw angles.

### **Composable Function**

The **`AccelerometerScreen`** composable function displays the real-time orientation data on the UI. It includes text elements showing the roll, pitch, and yaw angles, as well as a button to navigate to the **`GraphActivity`** for further visualization.

### **Database Interaction**

The **`saveOrientationData()`** function within the **`AccelerometerScreen`** composable saves the orientation data (roll, pitch, yaw, and timestamp) to the Room database using coroutines. This ensures that the data is persisted for future use.

## Database Schema

### Tables

1. **orientation_data**
    - `id`: Primary key, auto-generated unique identifier for each orientation data entry.
    - `roll`: Float, the roll angle representing the device's orientation around the X-axis.
    - `pitch`: Float, the pitch angle representing the device's orientation around the Y-axis.
    - `yaw`: Float, the yaw angle representing the device's orientation around the Z-axis.
    - `timestamp`: Long, timestamp indicating the time when the orientation data was collected.

## Queries Used

1. **Insert Data**:
    
    ```sql
    INSERT INTO orientation_data (roll, pitch, yaw, timestamp) VALUES (?, ?, ?, ?)
    
    ```
    

## **Retrieve Latest Data**

### **SQL Query**

```sql
sqlCopy code
SELECT * FROM orientation_data ORDER BY timestamp DESC LIMIT 50

```

- **Description**: Retrieves the most recent 50 orientation data entries from the database.
- **Ordering**: The data is ordered by timestamp in descending order, ensuring the latest entries are fetched first.

## **Retrieve All Data**

### **SQL Query**

```sql
sqlCopy code
SELECT * FROM orientation_data ORDER BY timestamp

```

- **Description**: Retrieves all orientation data entries from the database.
- **Ordering**: The data is ordered by timestamp in ascending order.

### **Graphing Library**

The activity utilizes the **`vico`** library for graphing. Specifically, it leverages the **`CartesianChartHost`** and related components provided by the **`vico`** library to visualize data in a Cartesian coordinate system.

### **Data Management**

- The activity maintains two lists of **`OrientationData`**: **`orientationDataList`** and **`orientationDataListfull`**, which are populated asynchronously from the database.
- The **`fetchDataFromDatabase()`** and **`fetchDataFromDatabasefull()`** methods retrieve orientation data from the Room database using coroutines.

### **File Operations**

- The activity handles exporting orientation data to a CSV file on external storage. It requests and checks for write storage permission before performing file operations.
- The **`exportDataToCSV()`** function constructs a CSV file containing orientation data and saves it to the Downloads directory.

### **User Interface**

- The **`GraphScreen`** composable function displays multiple graphs for roll, pitch, and yaw angles. Each graph is rendered using the **`Graph`** composable function.
- The **`Graph`** composable function accepts title, xData, and yData parameters to render a specific graph. It utilizes the **`CartesianChartHost`** to visualize the data series.

### **Permission Handling**

- The activity requests write storage permission (**`WRITE_EXTERNAL_STORAGE`**) from the user. If permission is granted, the activity proceeds with exporting data to CSV. Otherwise, it displays a message indicating permission denial.

### **Composable Functions**

- **`GraphScreen`**: Renders the layout for the graph activity, including multiple graphs and an export button.
- **`Graph`**: Renders an individual graph with a specified title, xData, and yData. It utilizes the **`CartesianChartHost`** to visualize the data series in a Cartesian coordinate system.

## Screenshots

![Untitled](facb%20f65cd6ec43c845f7ba6b10c95db884ef/Untitled%201.png)

![Untitled](facb%20f65cd6ec43c845f7ba6b10c95db884ef/Untitled%202.png)

![Untitled](facb%20f65cd6ec43c845f7ba6b10c95db884ef/Untitled%203.png)

## **Orientation Data Prediction**

This Python script demonstrates how to predict orientation data (roll, pitch, yaw) using linear regression. It reads orientation data from a CSV file, trains separate linear regression models for each orientation angle, and generates predictions for the next values based on the previous window of data.

### **Requirements**

- Python 3.x
- pandas
- numpy
- scikit-learn
- matplotlib

### **Usage**

1. Place your CSV files containing orientation data in the **`orientation_database`** folder.
2. Adjust the **`window_size`** variable to specify the number of previous data points to consider for prediction.
3. Run the script.

### **Script Overview**

- **Import Libraries**: Import required libraries including pandas, numpy, scikit-learn, matplotlib, and os.
- **Read Data**: Read orientation data from the first CSV file found in the **`orientation_database`** folder into a pandas DataFrame.
- **Data Preprocessing**: Set an index for the DataFrame and define the window size for prediction.
- **Model Training**: Train separate linear regression models for roll, pitch, and yaw angles using scikit-learn's **`LinearRegression`** class.
- **Generate Predictions**: Generate predictions for the next values of roll, pitch, and yaw angles using the trained models and the previous window of data.
- **Plot Predictions**: Plot the actual and predicted values of roll, pitch, and yaw angles using matplotlib.

### **Note**

- Ensure that your CSV files are formatted correctly with semicolon (**`;`**) as the delimiter.
- The script assumes that the CSV files contain columns named **`roll`**, **`pitch`**, and **`yaw`** representing orientation angles.
- Adjusting the **`window_size`** parameter can affect the accuracy of predictions. Experiment with different values to achieve the desired results.

![Untitled](facb%20f65cd6ec43c845f7ba6b10c95db884ef/Untitled%204.png)

![Untitled](facb%20f65cd6ec43c845f7ba6b10c95db884ef/Untitled%205.png)

![Untitled](facb%20f65cd6ec43c845f7ba6b10c95db884ef/Untitled%206.png)

![Untitled](facb%20f65cd6ec43c845f7ba6b10c95db884ef/Untitled%207.png)

github:[https://github.com/YashasviChaurasia/facb](https://github.com/YashasviChaurasia/facb)