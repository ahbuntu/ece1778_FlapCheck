#include <cmath>
#include <iostream>

#include "evm.h"

#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"

using std::vector;
using std::cout;
using std::endl;
using std::string;

using cv::Mat;
using cv::Size;
using cv::VideoCapture;
using cv::VideoWriter;
using cv::pyrDown;
using cv::split;

//using namespace cv;


void Evm::amplify_video(string in_file, string out_file) {

    //Load the frames from file
    cout << "Loading video..." << endl;
    std::vector<Mat> in_frames = load_frames(in_file);

    //Downsample the frames using a gaussian filter
    //  This reduces high frequency noise and the required computational workload
    cout << "Downsampling..." << endl;
    std::vector<Mat> gauss_down_frames = gauss_downsample_frames(in_frames, pyramid_levels);

    //Build the image stack
    //  Convert the frame sequence into an image stack (i.e. with a time dimension)
    //  This is used to perform temporal filtering on a per-pixel basis
    Mat gauss_down_stack = build_temporal_stack(gauss_down_frames);

    //Temporally filter and amplify the stack
    Mat down_filtered_stack = filter_temporal_stack(gauss_down_stack);

    //Break the stack back into frames
    //  Convert the stack back into a sequence of frames
    vector<Mat> down_filtered_frames = decompose_temporal_stack(down_filtered_stack, gauss_down_frames[0].size().height);

    //Upsample the frames using a gaussian filter
    cout << "Upsampling..." << endl;
    std::vector<Mat> filtered_frames = gauss_upsample_frames(down_filtered_frames, pyramid_levels);

    //Re-combine the filtered stack with the input frames
    //  Merge the filtered and original frames
    cout << "Filtering..." << endl;
    /*
     *vector<Mat> out_frames = combine_frames(in_frames, filtered_frames);
     */
    vector<Mat> out_frames = filtered_frames;

    //Write the output video
    cout << "Writing output..." << endl;
    write_frames(out_frames, default_fps, out_file);

}

vector<Mat> Evm::load_frames(string in_file) {
    VideoCapture cap(in_file);

    if(!cap.isOpened()) 
        throw "Could not open" + in_file;

    //Load all the frames
    //  This is bad for memory usage, but needed to due naieve FFT filtering
    int i = 0;
    std::vector<Mat> frames;
    while(true) {
        Mat frame;
        cap >> frame;

        if(i == 0) {
            Size frameSize = frame.size();
            cout << "Input Frame size: " << frameSize.width << "x" << frameSize.height << endl;
        }

        if(frame.empty())
            break;

        frames.push_back(frame);
        i++;
    }

    return frames;
}

void Evm::write_frames(const vector<Mat>& frames, float fps, string out_file) {
    //Write out the video
    VideoWriter vid_writer;

    for(const Mat& frame : frames) {

        if(!vid_writer.isOpened()) {
            vid_writer.open(out_file,
                            //cap.get(CV_CAP_PROP_FOURCC),
                            CV_FOURCC('X','V','I','D'),
                            //CV_FOURCC('M','J','P','G'),
                            fps,
                            frame.size());
        }

        if(frame.empty()) 
            break; 

        vid_writer << frame;
    }
}

vector<Mat> Evm::gauss_downsample_frames(const vector<Mat>& in_frames, int levels) {
    vector<Mat> gauss_down_frames;

    //Build a gaussian pyramid for each frame, and save the last (smallest) level
    int i = 0;
    for(const Mat& frame : in_frames) {
        Mat downsampled_frame = frame.clone();
        for(int level = 0; level < levels; level++) {
            //Downsample the frame by a factor of 2 in each dimension,
            //using a gaussian averaging function
            pyrDown(downsampled_frame, downsampled_frame);
            if(i == 0) {
                cout << "\tDown Pyramid Level " << level << " Size: " << downsampled_frame.size() << endl;
            }
        } 
        gauss_down_frames.push_back(downsampled_frame);
        i++;
    }

    return gauss_down_frames;
}

vector<Mat> Evm::gauss_upsample_frames(const vector<Mat>& in_frames, int levels) {
    vector<Mat> gauss_up_frames;

    //Build a gaussian pyramid for each frame, and save the last (smallest) level
    int i = 0;
    for(const Mat& frame : in_frames) {
        Mat upsampled_frame = frame.clone();
        for(int level = 0; level < levels; level++) {
            //Upsample the frame by a factor of 2 in each dimension,
            //using a gaussian averaging function
            pyrUp(upsampled_frame, upsampled_frame);
            if(i == 0) {
                cout << "\tUp Pyramid Level " << level << " Size: " << upsampled_frame.size() << endl;
            }

        } 
        gauss_up_frames.push_back(upsampled_frame);
        i++;
    }

    return gauss_up_frames;
}


Mat Evm::build_temporal_stack(const vector<Mat>& in_frames) {
    int nframes = (int) in_frames.size();
    int frame_width = in_frames.at(0).size().width;
    int frame_height = in_frames.at(0).size().height;


    //Create an image stack upon which we will perform a DFT.
    //  OpenCV supports performing a 'per-row' DFT on a 2D matrix,
    //  so we build a 2D matrix where each 'row' is a pixel frame
    //  with columns representing the values accross frames 
    //  
    //Therefore we build a matrix with:
    //   - frame_width*frame_height rows
    //   - nframes columns
    Mat temporal_stack(frame_width*frame_height, nframes, CV_8UC3);

    //Insert each frame into the stack
    for(int i = 0; i < (int) in_frames.size(); i++) {
        Mat frame_col = in_frames[i].reshape(3, frame_width*frame_height); //3 colour channels, npixel rows
        Mat stack_col = temporal_stack.col(i);

        frame_col.copyTo(stack_col);
    }

    return temporal_stack;
}

vector<Mat> Evm::decompose_temporal_stack(const Mat& temporal_stack, int frame_height) {
    vector<Mat> frames;

    Size stack_size = temporal_stack.size();
    int nframes = stack_size.width; //Number of columns

    for(int i = 0; i < nframes; i++) {
        Mat stack_col = temporal_stack.col(i).clone().reshape(3, frame_height);
        Mat frame;

        stack_col.copyTo(frame);

        frames.push_back(frame);
    }

    return frames;
}

Mat Evm::filter_temporal_stack(const Mat& temporal_stack) {
    //Filter each channel independantly
    vector<Mat> channels;
    split(temporal_stack, channels);

    vector<Mat> filtered_channels;

    int i = 0;
    for(const Mat& channel : channels) {
        if(i == 2) {
            filtered_channels.push_back(channel);
        } else {
            filtered_channels.push_back(Mat::zeros(channels[0].size(), CV_8U));
        }
        //Row-based DFT

        //Apply ideal band-pass filter

        //Row-based inverse DFT

        i++;
    }

    Mat filtered_temporal_stack;
    merge(filtered_channels, filtered_temporal_stack);

    return filtered_temporal_stack;
}

vector<Mat> Evm::combine_frames(const vector<Mat>& orig_frames, const vector<Mat>& filtered_frames) {
    return orig_frames;
}
