# BloodCare
This application is about Measuring (blood pressure) and Calculate the risk of hypertension

# How to Measure Blood Pressure
A preview for 30 seconds of recording will be processed frame by frame to get the intensities of RBG colors on each frame.

Red and Green intensities will be stored in an array that will be applied on a Fast Fourier Transform, on the resultant array the highest peak after neglecting the noise which will be on the first few stored data will contain the frequency of the heart rate on 1 second, after that the heart beat will be estimated. (Fft)
After estimating the heart rate, blood pressure can be estimated by using some equation which will be mentioned in the references.

# How to calculate the risk of hypertension?
After the blood pressure of user is detected, this application will calculate it with Naive Bayes algorithm
