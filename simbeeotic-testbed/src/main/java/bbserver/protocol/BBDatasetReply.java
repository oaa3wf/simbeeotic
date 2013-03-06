// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: proto/DSReply.proto

package bbserver.protocol;

public final class BBDatasetReply {
  private BBDatasetReply() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public static final class DSReply extends
      com.google.protobuf.GeneratedMessage {
    // Use DSReply.newBuilder() to construct.
    private DSReply() {
      initFields();
    }
    private DSReply(boolean noInit) {}
    
    private static final DSReply defaultInstance;
    public static DSReply getDefaultInstance() {
      return defaultInstance;
    }
    
    public DSReply getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return bbserver.protocol.BBDatasetReply.internal_static_bbserver_protocol_DSReply_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return bbserver.protocol.BBDatasetReply.internal_static_bbserver_protocol_DSReply_fieldAccessorTable;
    }
    
    // required uint32 process1 = 1;
    public static final int PROCESS1_FIELD_NUMBER = 1;
    private boolean hasProcess1;
    private int process1_ = 0;
    public boolean hasProcess1() { return hasProcess1; }
    public int getProcess1() { return process1_; }
    
    // required uint32 process2 = 2;
    public static final int PROCESS2_FIELD_NUMBER = 2;
    private boolean hasProcess2;
    private int process2_ = 0;
    public boolean hasProcess2() { return hasProcess2; }
    public int getProcess2() { return process2_; }
    
    // required uint32 process3 = 3;
    public static final int PROCESS3_FIELD_NUMBER = 3;
    private boolean hasProcess3;
    private int process3_ = 0;
    public boolean hasProcess3() { return hasProcess3; }
    public int getProcess3() { return process3_; }
    
    // required uint32 process4 = 4;
    public static final int PROCESS4_FIELD_NUMBER = 4;
    private boolean hasProcess4;
    private int process4_ = 0;
    public boolean hasProcess4() { return hasProcess4; }
    public int getProcess4() { return process4_; }
    
    // required uint32 process5 = 5;
    public static final int PROCESS5_FIELD_NUMBER = 5;
    private boolean hasProcess5;
    private int process5_ = 0;
    public boolean hasProcess5() { return hasProcess5; }
    public int getProcess5() { return process5_; }
    
    // required uint32 process6 = 6;
    public static final int PROCESS6_FIELD_NUMBER = 6;
    private boolean hasProcess6;
    private int process6_ = 0;
    public boolean hasProcess6() { return hasProcess6; }
    public int getProcess6() { return process6_; }
    
    // required uint32 process7 = 7;
    public static final int PROCESS7_FIELD_NUMBER = 7;
    private boolean hasProcess7;
    private int process7_ = 0;
    public boolean hasProcess7() { return hasProcess7; }
    public int getProcess7() { return process7_; }
    
    // required uint32 process8 = 8;
    public static final int PROCESS8_FIELD_NUMBER = 8;
    private boolean hasProcess8;
    private int process8_ = 0;
    public boolean hasProcess8() { return hasProcess8; }
    public int getProcess8() { return process8_; }
    
    // required uint32 fps = 9;
    public static final int FPS_FIELD_NUMBER = 9;
    private boolean hasFps;
    private int fps_ = 0;
    public boolean hasFps() { return hasFps; }
    public int getFps() { return fps_; }
    
    // required uint32 gyros1 = 10;
    public static final int GYROS1_FIELD_NUMBER = 10;
    private boolean hasGyros1;
    private int gyros1_ = 0;
    public boolean hasGyros1() { return hasGyros1; }
    public int getGyros1() { return gyros1_; }
    
    // required uint32 gyros2 = 11;
    public static final int GYROS2_FIELD_NUMBER = 11;
    private boolean hasGyros2;
    private int gyros2_ = 0;
    public boolean hasGyros2() { return hasGyros2; }
    public int getGyros2() { return gyros2_; }
    
    // required uint32 gyros3 = 12;
    public static final int GYROS3_FIELD_NUMBER = 12;
    private boolean hasGyros3;
    private int gyros3_ = 0;
    public boolean hasGyros3() { return hasGyros3; }
    public int getGyros3() { return gyros3_; }
    
    private void initFields() {
    }
    public final boolean isInitialized() {
      if (!hasProcess1) return false;
      if (!hasProcess2) return false;
      if (!hasProcess3) return false;
      if (!hasProcess4) return false;
      if (!hasProcess5) return false;
      if (!hasProcess6) return false;
      if (!hasProcess7) return false;
      if (!hasProcess8) return false;
      if (!hasFps) return false;
      if (!hasGyros1) return false;
      if (!hasGyros2) return false;
      if (!hasGyros3) return false;
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (hasProcess1()) {
        output.writeUInt32(1, getProcess1());
      }
      if (hasProcess2()) {
        output.writeUInt32(2, getProcess2());
      }
      if (hasProcess3()) {
        output.writeUInt32(3, getProcess3());
      }
      if (hasProcess4()) {
        output.writeUInt32(4, getProcess4());
      }
      if (hasProcess5()) {
        output.writeUInt32(5, getProcess5());
      }
      if (hasProcess6()) {
        output.writeUInt32(6, getProcess6());
      }
      if (hasProcess7()) {
        output.writeUInt32(7, getProcess7());
      }
      if (hasProcess8()) {
        output.writeUInt32(8, getProcess8());
      }
      if (hasFps()) {
        output.writeUInt32(9, getFps());
      }
      if (hasGyros1()) {
        output.writeUInt32(10, getGyros1());
      }
      if (hasGyros2()) {
        output.writeUInt32(11, getGyros2());
      }
      if (hasGyros3()) {
        output.writeUInt32(12, getGyros3());
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (hasProcess1()) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(1, getProcess1());
      }
      if (hasProcess2()) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(2, getProcess2());
      }
      if (hasProcess3()) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(3, getProcess3());
      }
      if (hasProcess4()) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(4, getProcess4());
      }
      if (hasProcess5()) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(5, getProcess5());
      }
      if (hasProcess6()) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(6, getProcess6());
      }
      if (hasProcess7()) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(7, getProcess7());
      }
      if (hasProcess8()) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(8, getProcess8());
      }
      if (hasFps()) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(9, getFps());
      }
      if (hasGyros1()) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(10, getGyros1());
      }
      if (hasGyros2()) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(11, getGyros2());
      }
      if (hasGyros3()) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(12, getGyros3());
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static bbserver.protocol.BBDatasetReply.DSReply parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static bbserver.protocol.BBDatasetReply.DSReply parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static bbserver.protocol.BBDatasetReply.DSReply parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static bbserver.protocol.BBDatasetReply.DSReply parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static bbserver.protocol.BBDatasetReply.DSReply parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static bbserver.protocol.BBDatasetReply.DSReply parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static bbserver.protocol.BBDatasetReply.DSReply parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static bbserver.protocol.BBDatasetReply.DSReply parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static bbserver.protocol.BBDatasetReply.DSReply parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static bbserver.protocol.BBDatasetReply.DSReply parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(bbserver.protocol.BBDatasetReply.DSReply prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private bbserver.protocol.BBDatasetReply.DSReply result;
      
      // Construct using bbserver.protocol.BBDatasetReply.DSReply.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new bbserver.protocol.BBDatasetReply.DSReply();
        return builder;
      }
      
      protected bbserver.protocol.BBDatasetReply.DSReply internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new bbserver.protocol.BBDatasetReply.DSReply();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return bbserver.protocol.BBDatasetReply.DSReply.getDescriptor();
      }
      
      public bbserver.protocol.BBDatasetReply.DSReply getDefaultInstanceForType() {
        return bbserver.protocol.BBDatasetReply.DSReply.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public bbserver.protocol.BBDatasetReply.DSReply build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private bbserver.protocol.BBDatasetReply.DSReply buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public bbserver.protocol.BBDatasetReply.DSReply buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        bbserver.protocol.BBDatasetReply.DSReply returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof bbserver.protocol.BBDatasetReply.DSReply) {
          return mergeFrom((bbserver.protocol.BBDatasetReply.DSReply)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(bbserver.protocol.BBDatasetReply.DSReply other) {
        if (other == bbserver.protocol.BBDatasetReply.DSReply.getDefaultInstance()) return this;
        if (other.hasProcess1()) {
          setProcess1(other.getProcess1());
        }
        if (other.hasProcess2()) {
          setProcess2(other.getProcess2());
        }
        if (other.hasProcess3()) {
          setProcess3(other.getProcess3());
        }
        if (other.hasProcess4()) {
          setProcess4(other.getProcess4());
        }
        if (other.hasProcess5()) {
          setProcess5(other.getProcess5());
        }
        if (other.hasProcess6()) {
          setProcess6(other.getProcess6());
        }
        if (other.hasProcess7()) {
          setProcess7(other.getProcess7());
        }
        if (other.hasProcess8()) {
          setProcess8(other.getProcess8());
        }
        if (other.hasFps()) {
          setFps(other.getFps());
        }
        if (other.hasGyros1()) {
          setGyros1(other.getGyros1());
        }
        if (other.hasGyros2()) {
          setGyros2(other.getGyros2());
        }
        if (other.hasGyros3()) {
          setGyros3(other.getGyros3());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                return this;
              }
              break;
            }
            case 8: {
              setProcess1(input.readUInt32());
              break;
            }
            case 16: {
              setProcess2(input.readUInt32());
              break;
            }
            case 24: {
              setProcess3(input.readUInt32());
              break;
            }
            case 32: {
              setProcess4(input.readUInt32());
              break;
            }
            case 40: {
              setProcess5(input.readUInt32());
              break;
            }
            case 48: {
              setProcess6(input.readUInt32());
              break;
            }
            case 56: {
              setProcess7(input.readUInt32());
              break;
            }
            case 64: {
              setProcess8(input.readUInt32());
              break;
            }
            case 72: {
              setFps(input.readUInt32());
              break;
            }
            case 80: {
              setGyros1(input.readUInt32());
              break;
            }
            case 88: {
              setGyros2(input.readUInt32());
              break;
            }
            case 96: {
              setGyros3(input.readUInt32());
              break;
            }
          }
        }
      }
      
      
      // required uint32 process1 = 1;
      public boolean hasProcess1() {
        return result.hasProcess1();
      }
      public int getProcess1() {
        return result.getProcess1();
      }
      public Builder setProcess1(int value) {
        result.hasProcess1 = true;
        result.process1_ = value;
        return this;
      }
      public Builder clearProcess1() {
        result.hasProcess1 = false;
        result.process1_ = 0;
        return this;
      }
      
      // required uint32 process2 = 2;
      public boolean hasProcess2() {
        return result.hasProcess2();
      }
      public int getProcess2() {
        return result.getProcess2();
      }
      public Builder setProcess2(int value) {
        result.hasProcess2 = true;
        result.process2_ = value;
        return this;
      }
      public Builder clearProcess2() {
        result.hasProcess2 = false;
        result.process2_ = 0;
        return this;
      }
      
      // required uint32 process3 = 3;
      public boolean hasProcess3() {
        return result.hasProcess3();
      }
      public int getProcess3() {
        return result.getProcess3();
      }
      public Builder setProcess3(int value) {
        result.hasProcess3 = true;
        result.process3_ = value;
        return this;
      }
      public Builder clearProcess3() {
        result.hasProcess3 = false;
        result.process3_ = 0;
        return this;
      }
      
      // required uint32 process4 = 4;
      public boolean hasProcess4() {
        return result.hasProcess4();
      }
      public int getProcess4() {
        return result.getProcess4();
      }
      public Builder setProcess4(int value) {
        result.hasProcess4 = true;
        result.process4_ = value;
        return this;
      }
      public Builder clearProcess4() {
        result.hasProcess4 = false;
        result.process4_ = 0;
        return this;
      }
      
      // required uint32 process5 = 5;
      public boolean hasProcess5() {
        return result.hasProcess5();
      }
      public int getProcess5() {
        return result.getProcess5();
      }
      public Builder setProcess5(int value) {
        result.hasProcess5 = true;
        result.process5_ = value;
        return this;
      }
      public Builder clearProcess5() {
        result.hasProcess5 = false;
        result.process5_ = 0;
        return this;
      }
      
      // required uint32 process6 = 6;
      public boolean hasProcess6() {
        return result.hasProcess6();
      }
      public int getProcess6() {
        return result.getProcess6();
      }
      public Builder setProcess6(int value) {
        result.hasProcess6 = true;
        result.process6_ = value;
        return this;
      }
      public Builder clearProcess6() {
        result.hasProcess6 = false;
        result.process6_ = 0;
        return this;
      }
      
      // required uint32 process7 = 7;
      public boolean hasProcess7() {
        return result.hasProcess7();
      }
      public int getProcess7() {
        return result.getProcess7();
      }
      public Builder setProcess7(int value) {
        result.hasProcess7 = true;
        result.process7_ = value;
        return this;
      }
      public Builder clearProcess7() {
        result.hasProcess7 = false;
        result.process7_ = 0;
        return this;
      }
      
      // required uint32 process8 = 8;
      public boolean hasProcess8() {
        return result.hasProcess8();
      }
      public int getProcess8() {
        return result.getProcess8();
      }
      public Builder setProcess8(int value) {
        result.hasProcess8 = true;
        result.process8_ = value;
        return this;
      }
      public Builder clearProcess8() {
        result.hasProcess8 = false;
        result.process8_ = 0;
        return this;
      }
      
      // required uint32 fps = 9;
      public boolean hasFps() {
        return result.hasFps();
      }
      public int getFps() {
        return result.getFps();
      }
      public Builder setFps(int value) {
        result.hasFps = true;
        result.fps_ = value;
        return this;
      }
      public Builder clearFps() {
        result.hasFps = false;
        result.fps_ = 0;
        return this;
      }
      
      // required uint32 gyros1 = 10;
      public boolean hasGyros1() {
        return result.hasGyros1();
      }
      public int getGyros1() {
        return result.getGyros1();
      }
      public Builder setGyros1(int value) {
        result.hasGyros1 = true;
        result.gyros1_ = value;
        return this;
      }
      public Builder clearGyros1() {
        result.hasGyros1 = false;
        result.gyros1_ = 0;
        return this;
      }
      
      // required uint32 gyros2 = 11;
      public boolean hasGyros2() {
        return result.hasGyros2();
      }
      public int getGyros2() {
        return result.getGyros2();
      }
      public Builder setGyros2(int value) {
        result.hasGyros2 = true;
        result.gyros2_ = value;
        return this;
      }
      public Builder clearGyros2() {
        result.hasGyros2 = false;
        result.gyros2_ = 0;
        return this;
      }
      
      // required uint32 gyros3 = 12;
      public boolean hasGyros3() {
        return result.hasGyros3();
      }
      public int getGyros3() {
        return result.getGyros3();
      }
      public Builder setGyros3(int value) {
        result.hasGyros3 = true;
        result.gyros3_ = value;
        return this;
      }
      public Builder clearGyros3() {
        result.hasGyros3 = false;
        result.gyros3_ = 0;
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:bbserver.protocol.DSReply)
    }
    
    static {
      defaultInstance = new DSReply(true);
      bbserver.protocol.BBDatasetReply.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:bbserver.protocol.DSReply)
  }
  
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_bbserver_protocol_DSReply_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_bbserver_protocol_DSReply_fieldAccessorTable;
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\023proto/DSReply.proto\022\021bbserver.protocol" +
      "\"\326\001\n\007DSReply\022\020\n\010process1\030\001 \002(\r\022\020\n\010proces" +
      "s2\030\002 \002(\r\022\020\n\010process3\030\003 \002(\r\022\020\n\010process4\030\004" +
      " \002(\r\022\020\n\010process5\030\005 \002(\r\022\020\n\010process6\030\006 \002(\r" +
      "\022\020\n\010process7\030\007 \002(\r\022\020\n\010process8\030\010 \002(\r\022\013\n\003" +
      "fps\030\t \002(\r\022\016\n\006gyros1\030\n \002(\r\022\016\n\006gyros2\030\013 \002(" +
      "\r\022\016\n\006gyros3\030\014 \002(\rB\020B\016BBDatasetReply"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_bbserver_protocol_DSReply_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_bbserver_protocol_DSReply_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_bbserver_protocol_DSReply_descriptor,
              new java.lang.String[] { "Process1", "Process2", "Process3", "Process4", "Process5", "Process6", "Process7", "Process8", "Fps", "Gyros1", "Gyros2", "Gyros3", },
              bbserver.protocol.BBDatasetReply.DSReply.class,
              bbserver.protocol.BBDatasetReply.DSReply.Builder.class);
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }
  
  public static void internalForceInit() {}
  
  // @@protoc_insertion_point(outer_class_scope)
}