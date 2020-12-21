//
// Created by zfz on 12/20/2020.
//

#include "data_manager.h"
#include "stdio.h"

namespace MyData {
DataManager::DataManager() {
  printf("****Data manager init");
}

DataManager::~DataManager() {
  printf("****Data manager deconstruct");
}

void DataManager::Test() {
  printf("****Data test!");
}
}