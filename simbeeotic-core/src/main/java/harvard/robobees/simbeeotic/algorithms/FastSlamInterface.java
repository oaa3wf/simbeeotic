/*
 * Copyright (c) 2012, The President and Fellows of Harvard College.
 * All Rights Reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *  3. Neither the name of the University nor the names of its contributors
 *     may be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE UNIVERSITY AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE UNIVERSITY OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */


package harvard.robobees.simbeeotic.algorithms;

import Jama.Matrix;

/**
 * @author Mburkardt
 */

public interface FastSlamInterface {

    public void initialize(Matrix stateVector, int numFeatures, Matrix covariance, Matrix controls, Matrix measurements);
    public void predict(Matrix stateVector, int numFeatures, Matrix covariance, Matrix controls, Matrix measurements);
    public double getImporanceFactor(Matrix muMinusX, Matrix covariance);
    public void ekfInitialize(Matrix stateVector, Matrix covariance, Matrix controls, Matrix measurements);
    public void ekfPredict(Matrix stateVector, Matrix controls, Matrix covariance);
    public void ekfUpdate(Matrix stateVector, Matrix controls, Matrix covariance, Matrix measurements, int landmarkIndex);
    public void addLandmarks(Matrix stateVector, Matrix covariance, Matrix newLandmarks, Matrix controls);
    public Matrix getCovariance();
    public void setCovariance(Matrix covariance);
    public Matrix getStateVector();
    public void setStateVector(Matrix stateVector);
}