################################################################################
# Copyright (c) 2015-2018 Skymind, Inc.
#
# This program and the accompanying materials are made available under the
# terms of the Apache License, Version 2.0 which is available at
# https://www.apache.org/licenses/LICENSE-2.0.
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations
# under the License.
#
# SPDX-License-Identifier: Apache-2.0
################################################################################


import unittest

import jumpy as jp
import numpy as np


import gc
gc.disable()


class TestArrayCreation(unittest.TestCase):

    def setUp(self):
        self.x_np = np.random.random((100, 32, 16))
        self.x_jp = jp.array(self.x_np)
        self.x_np_2 = x_jp.numpy()

    def test_arr_creation(self):
        self.assertEquals(self.x_np.shape, self.x_jp.shape)
        self.assertEquals(self.x_np.shape, self.x_np_2.shape)
        x_np = self.x_np.ravel()
        x_np_2 = x_np_2.ravel()
        assertEquals(list(x_np), list(x_np_2))
