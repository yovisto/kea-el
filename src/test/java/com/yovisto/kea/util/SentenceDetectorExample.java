package com.yovisto.kea.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

import org.junit.Test;

public class SentenceDetectorExample {

	
		@Test
		public void sometest(){
			SentenceDetectorME sentenceDetector = null;
			
			InputStream modelIn = this.getClass().getClassLoader().getResourceAsStream("en-sent.bin");
			try {
				SentenceModel model = new SentenceModel(modelIn);
				sentenceDetector = new SentenceDetectorME(model);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (modelIn != null) {
					try {
						modelIn.close();
					} catch (IOException e) {
					}
				}
			}
			String text = "Mourners in Michigan welcome body of Ford  \nEx-president 'who meant so much' scheduled to be buried Wednesday \n \nGRAND RAPIDS, Mich. - Moving quietly and solemnly through the moonlight, mourners waited for a chance to file past the casket of Gerald R. Ford, welcoming home the 38th president for a final time and celebrating a man they said embodied Midwest values. \n \n“This is a once-in-lifetime opportunity to pay tribute,” said Karin Lewis, 44, who brought her five boys, ages 6 to 15, to Ford’s presidential museum to pay respects Tuesday night. “He meant so much to this community.” \n \nFord’s state funeral was to end Wednesday afternoon after a service at Grace Episcopal Church in East Grand Rapids. His body was to be interred during a private burial overlooking the Grand River north of the museum on the museum grounds. \n \nDonald Rumsfeld, who served in Ford’s cabinet as his chief of staff and as his defense secretary, was to deliver a eulogy. Former President Jimmy Carter, who defeated Ford in 1976 but later became a close friend of his former opponent, and Richard Norton Smith, who used to be the director of the Ford museum and presidential library, also were scheduled to speak. \n \nFinal salute \nOn Tuesday, members of the public jammed streets and waved as Ford’s casket was carried from the Grand Rapids airport, where it arrived following services at Washington National Cathedral. \n \n“You were a paradoxical gift of remarkable intellect and achievement wrapped in a plain brown wrapper,” Michigan Gov. Jennifer Granholm said of Ford. “Welcome home to the people that you reflected so well when you were in Washington.” \n \nLater, members of the public walked from the DeVos Place convention center across a bridge over the Grand River to the Gerald R. Ford Presidential Library and Museum for a final salute. \n \nSome wore formal suits and dresses. Others wore sweat shirts from University of Michigan, where Ford played center on the Wolverines’ undefeated national championship football teams of 1932 and 1933. \n \n“I grew up in Grand Rapids, and President Ford was our legacy,” said Bobbe Taber, 43, who now lives in the Kalamazoo area but came to Grand Rapids to see Ford’s casket. \n \nMichigan National Guard spokeswoman Lynn Chapp said 60,000 people were expected to walk by the casket from the time repose began around 6 p.m. Tuesday until it was scheduled to end at 11 a.m. Wednesday. Lines snaked along several blocks in the city’s downtown as people waited their turn Tuesday night. \n \n'The best of America' \nThe service Wednesday at his hometown church, which seats about 350, was to be much smaller than Tuesday’s elaborate national funeral service in Washington, which drew 3,000 people. President Bush and his father spoke Tuesday, as did NBC newsman Tom Brokaw and Ford’s secretary of state, Henry Kissinger, among others. \n \n“In President Ford, the world saw the best of America, and America found a man whose character and leadership would bring calm and healing to one of the most divisive moments in our nation’s history,” President Bush said in his eulogy. \n \nBush’s father, the first President Bush, called Ford a “Norman Rockwell painting come to life” and pierced the solemnity of the occasion by cracking gentle jokes about Ford’s reputation as an errant golfer. He said Ford knew his golf game was getting better when he began hitting fewer spectators. \n \nUnder towering arches of the cathedral, Kissinger paid tribute to Ford’s leadership in achieving nuclear arms control with the Soviets, pushing for the first political agreement between Israel and Egypt and helping to bring majority rule to southern Africa. \n \n“In his understated way he did his duty as a leader, not as a performer playing to the gallery,” Kissinger said. “Gerald Ford had the virtues of small town America.” \n \nBrokaw said in his eulogy that Ford brought to office “no demons, no hidden agenda, no hit list or acts of vengeance,” an oblique reference to the air of subterfuge that surrounded Nixon in his final days. \n \nFord’s athletic interest was honored, too, in the capital and in Michigan. At the Grand Rapids airport that bears Ford’s name, the University of Michigan band played the school’s famous fight song, “The Victors,” as Ford’s flag-draped casket was transferred to a hearse. \n \nFord died at 93 on Dec. 26 at his home in Rancho Mirage, Calif. \n\n\n";
			List<String> sentences = java.util.Arrays.asList(sentenceDetector.sentDetect(text));
			System.out.println(sentences);
			
			Span[] spans = sentenceDetector.sentPosDetect(text);
			
			for (Span s : spans){
				System.out.println(s.getStart() + "-" + s.getEnd() + " '" + text.substring(s.getStart(), s.getEnd()) + "'");
				
			}
			
		}
}
