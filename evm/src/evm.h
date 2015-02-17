#pragma once
#include <string>
#include <vector>
#include <opencv2/core/core.hpp>

class Evm {
    public:
        Evm()
            : amp_factor(0.2)
            , freq_min(0.8)
            , freq_max(1.0)
            //, freq_min(0.1)
            //, freq_max(1.0)
            , pyramid_levels(4)
            , default_fps(30)
        {}

        void amplify_video(std::string in_file, std::string out_file);


    private:
        std::vector<cv::Mat> load_frames(std::string in_file);
        void write_frames(const std::vector<cv::Mat>& frames, float fps, std::string out_file);

        std::vector<cv::Mat> gauss_downsample_frames(const std::vector<cv::Mat>& in_frames, int levels);
        std::vector<cv::Mat> gauss_upsample_frames(const std::vector<cv::Mat>& in_frames, int levels);

        cv::Mat build_temporal_stack(const std::vector<cv::Mat>& in_frames);
        std::vector<cv::Mat> decompose_temporal_stack(const cv::Mat& temporal_stack, int frame_height);

        cv::Mat filter_temporal_stack(const cv::Mat& temporal_stack, const cv::Mat& filter_response);

        std::vector<cv::Mat> merge_frames(const std::vector<cv::Mat>& orig_frames, const std::vector<cv::Mat>& filtered_frames, float alpha);


        cv::Mat getAllPassFilter(cv::Size size, int type);
        cv::Mat getBandPassFilter(cv::Size size, int type, float freq_low, float freq_high, float sample_rate);

        //Options
        float amp_factor;
        float freq_min;
        float freq_max; 
        int pyramid_levels;
        float default_fps;

        //Working Data
        //std::vector<cv::Mat> frames;
};
