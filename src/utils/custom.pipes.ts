import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'getPostThumbnail'})
export class GetPostThumbnailPipe implements PipeTransform {
        transform(value: string) {
            let paths = value.split("--:--");
            return paths[1];
        }
}