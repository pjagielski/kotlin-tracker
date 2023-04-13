(
SynthDef(\tb404, {
    |bus, freq=440, wave=1, cutoff=2000, res=0.15, start=600, sus=0.25, dec=0.1, dur=1, amp=1, pan=0|
    var sound;
	var line = Line.ar(1, 0, dur*2, doneAction: 2);
	var volEnv = EnvGen.ar(Env.perc(10e-10, dur*2), doneAction: 2);
    var filEnv = EnvGen.ar(Env.adsr(10e-10, dec, sus, dur*2), line);
    var waves = [Saw.ar(freq), Pulse.ar(freq, 0.5)];
	sound = RLPF.ar(Select.ar(wave, waves), start + (filEnv * cutoff), res);
	sound = (sound.clip2(0.4) * 10).distort();
	Out.ar(bus, Pan2.ar(sound, pan, amp * volEnv));
}).add;

SynthDef(\piano,{
	|bus, freq=440, dur=1, pan, amp=1, detune=0.1, muffle=1, stereo=0.2, mul=1  |
	var env = EnvGen.ar(Env.linen(0.002, 0.996, 0.002, 1,-3), timeScale:dur, doneAction:0);
	// the +0.01 to freq is because of edge case rounding internal to the MdaPiano synth
	var sound = MdaPiano.ar(freq+0.01, vel:amp*100, hard:0.8*amp, decay:0.1*dur,
		tune:0.5, random:0.05, stretch:detune, muffle:0.8*muffle, stereo:stereo, mul: mul);
	Out.ar(bus, Pan2.ar(sound, pan, env))
}).add;

SynthDef.new(\play1,
    {|amp=1, sus=1, blur=1, pan=0, rate=1.0, bus=0, buf=0|
        var osc, env;
        sus = sus * blur;
        osc = PlayBuf.ar(1, buf, (BufRateScale.ir(buf) * rate), doneAction: Done.freeSelf);
        osc = (osc * amp);
        osc = Mix(osc) * 0.5;
        osc = Pan2.ar(osc, pan);
        Out.ar(bus, osc)
}).add;

SynthDef.new(\play2,
    {|amp=1, sus=1, blur=1, pan=0, rate=1.0, bus=0, buf=0|
        var osc, env;
        sus = sus * blur;
        osc = PlayBuf.ar(2, buf, (BufRateScale.ir(buf) * rate), doneAction: Done.freeSelf);
        osc = (osc * amp);
        osc = Mix(osc) * 0.5;
        osc = Pan2.ar(osc, pan);
        Out.ar(bus, osc)
}).add;

SynthDef.new(\sampler,
    {|bus=0, bpm=120, total=4, beats=4, start=0, amp=1, cutoff=10000, buf=0|
        var beatLen, dur, env, rate, frames, pos, snd;
        beatLen = 60 / bpm;
        dur = beats * beatLen;
        env = EnvGen.ar(Env([1,1,0], [dur-0.2, 0.2], 'welch'));
        rate = BufDur.kr(buf) / (total * beatLen);
        frames = BufFrames.kr(buf);
        pos = (start / total) * frames;
        snd = PlayBuf.ar(2, buf, (rate * BufRateScale.ir(buf)), startPos: pos, doneAction: Done.freeSelf);
        snd = LPF.ar(snd, cutoff);
        Out.ar(bus, Pan2.ar(snd * amp, 0, env));
}).add;

)

s.options.useSystemClock = true;


Synth(\tb404, [\dur, 0.25, \cutoff, 100, \start, 100])
Synth(\piano)

(
var dir = PathName.new(thisProcess.nowExecutingPath).pathOnly;
b = Buffer.read(s, dir +/+ "bd.wav");
)
Synth(\play2, [\buf, b])

s.sendMsg("/s_new", "tb404", -1, 0, 0);
