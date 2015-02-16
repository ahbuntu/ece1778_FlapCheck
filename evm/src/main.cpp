#include <iostream>

#include "evm.h"

using namespace std;

int main(int argc, char* argv[]) {
    if(argc != 3) {
        cout << "Usage: " << argv[0] << " in_file out_file" << endl; 
        return 1;
    }

    Evm evm;

    evm.amplify_video(argv[1], argv[2]);

    return 0;
}
