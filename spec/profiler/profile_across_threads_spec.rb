
require 'spec/profiler/profiler_spec_helpers'

describe JRuby::Profiler, "profile data across threads" do
  include JRuby::Profiler::SpecHelpers

  before do
    config = org.jruby.RubyInstanceConfig.new
    config.processArguments(["--profile"].to_java(java.lang.String))
    @runtime = org.jruby.Ruby.newInstance(config)
    org.jruby.RubyInstanceConfig.PROTECT_PROFILING_DATA = true
  end

  it "should print profile data from all threads" do
    @runtime.evalScriptlet <<-END
def foo
  sleep 1
end

def bar
  sleep 1
end

foo

Thread.new do
  bar
end.join
END
    output_stream = java.io.ByteArrayOutputStream.new
    @runtime.printProfilerOutput(java.io.PrintStream.new(output_stream))

    output_stream.to_s.should =~ /Object#foo/
    output_stream.to_s.should =~ /Object#bar/
  end
end
