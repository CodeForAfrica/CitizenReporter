import { PipeTransform, Pipe } from '@angular/core';

@Pipe({name: 'summary'})
export class SummaryPipe implements PipeTransform{

  transform(value: string): string {
    if(value){
      return value.substring(0, 87) + " ...";
    }
    return value;
  }
}
